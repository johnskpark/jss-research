package app.evolution.multilevel;

import ec.Breeder;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.util.Parameter;
import ec.util.RandomChoice;
import ec.util.ThreadPool;

// In multilevel selection, the subpopulations represent the groups,
// in which individuals can be a part of.

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */

// FIXME Refactor the threading sometime later down the line.
public class MLSBreeder extends Breeder {

	private static final long serialVersionUID = -6914152113435773281L;

	public static final String P_ELITE = "elite";
    public static final String P_ELITE_FRAC = "elite-fraction";
    public static final String P_REEVALUATE_ELITES = "reevaluate-elites";
    public static final String P_SEQUENTIAL_BREEDING = "sequential";
    public static final String P_CLONE_PIPELINE_AND_POPULATION = "clone-pipeline-and-population";
    public static final String P_CROSSOVER_PROB = "crossover-prob";
    public static final String P_MUTATION_PROB = "mutation-prob";

    public static final int NOT_SET = -1;

    public static final int CROSSOVER_PARENTS_REQUIRED = 2;
    public static final int MUTATION_PARENTS_REQUIRED = 1;
    public static final int CROSSOVER_INDS_PRODUCED = 2;
    public static final int MUTATION_INDS_PRODUCED = 1;

    /** An array[subpop] of the number of elites to keep for that subpopulation */
    private int[] elite;
    private double[] eliteFrac;
    private boolean[] reevaluateElites;
    private boolean sequentialBreeding;
    private boolean clonePipelineAndPopulation;
    private Population backupPopulation = null;

    // TODO temporary default parameters.
    private double groupCrossoverRate = 0.9;
    private double groupMutationRate = 0.1;

    // Temporary field variable to store the number of individuals
    // in each subpopulation in the meta population.
    private int[] numIndividualsPerSubpop;

    public boolean usingElitism(int subpopulation) {
    	return (elite[subpopulation] > 0 ) || (eliteFrac[subpopulation] > 0);
    }

    public int numElites(EvolutionState state, int subpopulation) {
    	if (elite[subpopulation] != NOT_SET) {
    		return elite[subpopulation];
    	}
    	else if (eliteFrac[subpopulation] == 0) {
    		return 0; // no elites
    	}
    	else if (eliteFrac[subpopulation] != NOT_SET) {
    		return (int) Math.max(Math.floor(state.population.subpops[subpopulation].individuals.length * eliteFrac[subpopulation]), 1.0);  // AT LEAST 1 ELITE
    	}
    	else {
    		state.output.warnOnce("Elitism error (SimpleBreeder).  This shouldn't be able to happen.  Please report.");
    		return 0;  // this shouldn't happen
    	}
    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
    	Parameter p = new Parameter(Initializer.P_POP).push(Population.P_SIZE);
    	int size = state.parameters.getInt(p,null,1);  // if size is wrong, we'll let Population complain about it -- for us, we'll just make 0-sized arrays and drop out.

    	eliteFrac = new double[size];
    	elite = new int[size];
    	for(int i = 0; i < size; i++) {
    		eliteFrac[i] = elite[i] = NOT_SET;
    	}
    	reevaluateElites = new boolean[size];

    	sequentialBreeding = state.parameters.getBoolean(base.push(P_SEQUENTIAL_BREEDING), null, false);
    	if (sequentialBreeding && (size == 1)) { // uh oh, this can't be right
    		state.output.fatal("The Breeder is breeding sequentially, but you have only one population.", base.push(P_SEQUENTIAL_BREEDING));
    	}

    	clonePipelineAndPopulation =state.parameters.getBoolean(base.push(P_CLONE_PIPELINE_AND_POPULATION), null, true);
    	if (!clonePipelineAndPopulation && (state.breedthreads > 1)) { // uh oh, this can't be right
    		state.output.fatal("The Breeder is not cloning its pipeline and population, but you have more than one thread.", base.push(P_CLONE_PIPELINE_AND_POPULATION));
    	}

    	int defaultSubpop = state.parameters.getInt(new Parameter(Initializer.P_POP).push(Population.P_DEFAULT_SUBPOP), null, 0);
    	for(int x=0;x<size;x++) {
    		// get elites
    		if (state.parameters.exists(base.push(P_ELITE).push(""+x),null)) {
    			if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null)) {
    				state.output.error("Both elite and elite-frac specified for subpouplation " + x + ".", base.push(P_ELITE_FRAC).push(""+x), base.push(P_ELITE_FRAC).push(""+x));
    			} else {
    				elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+x),null,0);
    				if (elite[x] < 0) {
    					state.output.error("Elites for subpopulation " + x + " must be an integer >= 0", base.push(P_ELITE).push(""+x));
    				}
    			}
    		} else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null)) {
    			eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+x),null,0.0, 1.0);
    			if (eliteFrac[x] < 0.0) {
    				state.output.error("Elite Fraction of subpopulation " + x + " must be a real value between 0.0 and 1.0 inclusive", base.push(P_ELITE_FRAC).push(""+x));
    			}
    		} else if (defaultSubpop >= 0) {
    			if (state.parameters.exists(base.push(P_ELITE).push(""+defaultSubpop),null)) {
    				elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+defaultSubpop),null,0);
    				if (elite[x] < 0) {
    					state.output.warning("Invalid default subpopulation elite value.");  // we'll fail later
    				}
    			} else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null)) {
    				eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null,0.0, 1.0);
    				if (eliteFrac[x] < 0.0) {
    					state.output.warning("Invalid default subpopulation elite-frac value.");  // we'll fail later
    				}
    			} else { // elitism is 0
    				elite[x] = 0;
    			}
    		} else { // elitism is 0
    			elite[x] = 0;
    		}

    		// get reevaluation
    		if (defaultSubpop >= 0 && !state.parameters.exists(base.push(P_REEVALUATE_ELITES).push(""+x),null)) {
    			reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+defaultSubpop), null, false);
    			if (reevaluateElites[x]) {
    				state.output.warning("Elite reevaluation not specified for subpopulation " + x + ".  Using values for default subpopulation " + defaultSubpop + ": " + reevaluateElites[x]);
    			}
    		} else {
    			reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+x), null, false);
    		}
    	}

    	// TODO get the crossover and mutation rates.

    	state.output.exitIfErrors();
    }

	@Override
	public Population breedPopulation(final EvolutionState state) {
		// In multilevel selection, breeding occurs in two stages.
		// The first stage breeds the groups and individuals so
		// so that there are double the numbers of groups and
		// individuals in the intermediate population. Afterwards,
		// half the groups and individuals are removed from the
		// intermediate population to obtain the next generation.

		Population newPop = null;
		if (clonePipelineAndPopulation) {
			newPop = (Population) state.population.emptyClone();
		} else {
			if (backupPopulation == null) {
				backupPopulation = (Population) state.population.emptyClone();
			}
			newPop = backupPopulation;
			newPop.clear();
			backupPopulation = (Population) state.population;  // swap in
		}

		Subpopulation[] interSubpops = new Subpopulation[state.population.subpops.length];

		// TODO

		return null;
	}

	public Population breedMetaPopulation(final EvolutionState state, final Population metaPop) {
		Population newPop = null;

		if (clonePipelineAndPopulation) {
			newPop = (Population) metaPop.emptyClone();
		} else {
			if (backupPopulation == null) {
				backupPopulation = (Population) metaPop.emptyClone();
			}
			newPop = backupPopulation;
			newPop.clear();
			backupPopulation = (Population) metaPop;
		}

		numIndividualsPerSubpop = new int[newPop.subpops.length];

		// Load the individuals into the meta population
		loadPopulation(state, newPop);
		breedSubpopulations(state, newPop);
		breedIndividuals(state, newPop);

		return newPop;
	}

	protected void loadPopulation(EvolutionState state, Population pop) {
		int buffer = ((MLSEvolutionState) state).getTotalNumIndividuals();

		// Load in the subpopulations into the meta population.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			pop.subpops[i] = (Subpopulation) subpop.emptyClone();
			pop.subpops[i].individuals = new Individual[subpop.individuals.length + buffer];

			numIndividualsPerSubpop[i] = subpop.individuals.length;

			for (int j = 0; j < subpop.individuals.length; j++) {
				pop.subpops[i].individuals[j] = (Individual) subpop.individuals[j].clone();
			}
		}
	}

	// Updates the population provided in the reference.
	protected void breedSubpopulations(EvolutionState state, Population pop) {
		// The maximum number of threads required is the number of subpopulations to breed.
		int subpopDiff = pop.subpops.length - state.population.subpops.length;
		int numThreads = Math.min(subpopDiff, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int numGroups[] = new int[numThreads];
		int from[] = new int[numThreads];

		int subpopsPerThread = subpopDiff / numThreads;
		int slop = subpopDiff - numThreads * subpopsPerThread;
		int currentFrom = 0;

		for (int thread = 0; thread < numThreads; thread++) {
			if (slop > 0) {
				numGroups[thread] = subpopsPerThread + 1;
				slop--;
			} else {
				numGroups[thread] = subpopsPerThread;
			}

			if (numGroups[thread] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}

			from[thread] = currentFrom;
			currentFrom += numGroups[thread];
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedSubpopChunk(pop, state, numGroups[0], from[0], 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				SubpopBreederThread r = new SubpopBreederThread();
				r.newpop = pop;
				r.numGroup = numGroups[i];
				r.from = from[i];
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}
	}

	protected void breedSubpopChunk(Population newpop,
			EvolutionState state,
			int numGroup,
			int from,
			int threadnum) {
		// FIXME The breeding of the subpopulations is hard coded into the breeder.
		// This will need to be fixed sometime later down the line.
		int index = from;
		int upperBound = from + numGroup;
		while (index < upperBound) {
			index += produceSubpop(1, upperBound - index, index, newpop, state, threadnum);

			if (index > upperBound) {
				state.output.fatal("A breeding pipeline overwrote the space of another pipeline in the population.  You need to check your breeding pipeline code (in produce() ).");
			}
		}
	}

	protected int produceSubpop(final int min,
			final int max,
			final int index,
			final Population newpop,
			final EvolutionState state,
			final int threadnum) {
		double value = state.random[threadnum].nextDouble();
		int total = 0;

		if (value < groupCrossoverRate) {
			total = produceSubpopCrossover(min, max, index, newpop, state, threadnum);
		} else if (value < groupCrossoverRate + groupMutationRate) { // Should always happen for the current code.
			total = produceSubpopMutation(min, max, index, newpop, state, threadnum);
		} else {
			state.output.fatal("The possible methods of producing subpopulations do not sufficiently cover for the random value.");
		}

		return total;
	}

	protected int produceSubpopCrossover(final int min,
			final int max,
			final int start,
			final Population newpop,
			final EvolutionState state,
			final int threadnum) {
		if (start < CROSSOVER_PARENTS_REQUIRED) {
			state.output.fatal("There are insufficient number of subpopulations to generate new subpopulations. Number of subpopulations: " + start + ", subpopulations required: " + CROSSOVER_PARENTS_REQUIRED);
		}

		int n = CROSSOVER_INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		int index = start;
		while (index < n + start) { // To be perfectly honest, this loop isn't necessary.
			// Select two random subpopulations from 0 to (start-1) to crossover based on their fitness.
			double[] fitnesses1 = new double[start];
			double[] fitnesses2 = new double[start-1];
			for (int j = 0; j < start - 1; j++) {
				fitnesses1[j] = fitnesses2[j] = ((MLSSubpopulation) newpop.subpops[j]).getFitness().fitness();
			}
			fitnesses1[start-1] = ((MLSSubpopulation) newpop.subpops[start-1]).getFitness().fitness();

			// Randomly select the parents for crossover.
			int parentIndex1 = selectFitness(fitnesses1, state.random[threadnum].nextDouble());

			// Replace the selected parent with the subpopulation at start-1 so that its not considered for selection.
			if (parentIndex1 != start - 1) {
				fitnesses2[parentIndex1] = fitnesses2[start-1];
			}
			int parentIndex2 = selectFitness(fitnesses2, state.random[threadnum].nextDouble());

			if (parentIndex1 == parentIndex2) {
				parentIndex2 = start-1;
			}

			Subpopulation[] parents = new Subpopulation[]{newpop.subpops[parentIndex1], newpop.subpops[parentIndex2]};

			// Arbitrarily select the individuals to exchange in between the two subpopulations.
			int[] swapIndices = new int[]{
					state.random[threadnum].nextInt(parents[0].individuals.length),
					state.random[threadnum].nextInt(parents[1].individuals.length)
			};
			int length = CROSSOVER_PARENTS_REQUIRED;

			// Only generate as many children as required.
			MLSSubpopulation[] children = new MLSSubpopulation[n];
			for (int child = 0; child < n; child++) {
				int parentIndex = child % length;

				// Populate the child subpopulation with individuals.
				children[child] = (MLSSubpopulation) parents[parentIndex].emptyClone();

				for (int ind = 0; ind < parents[child%length].individuals.length; ind++) {
					if (ind == swapIndices[child]) {
						children[child].individuals[ind] = parents[(child+1)%length].individuals[swapIndices[(child+1)%length]];
					} else {
						children[child].individuals[ind] = parents[parentIndex].individuals[ind];
					}
				}

				// Evaluate the child subpopulation.
				((MLSEvaluator) state.evaluator).evaluateSubpopulation(state, children[child]);

				// Add the subpopulation to the population
				newpop.subpops[index+child] = children[child];

				numIndividualsPerSubpop[index+child] = parents[parentIndex].individuals.length;
			}

			index += n;
		}

		return n;
	}

	protected int produceSubpopMutation(final int min,
			final int max,
			final int start,
			final Population newpop,
			final EvolutionState state,
			final int threadnum) {
		if (start < MUTATION_PARENTS_REQUIRED) {
			state.output.fatal("There are insufficient number of subpopulations to generate new subpopulations. Number of subpopulations: " + start + ", subpopulations required: " + MUTATION_PARENTS_REQUIRED);
		}

		int n = MUTATION_INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		int index = start;
		while (index < n + start) {
			// Select a random subpopulation from 0 to (start-1) to mutate based on their fitness.

			double[] fitnesses = new double[start];
			for (int j = 0; j < start; j++) {
				fitnesses[j] = ((MLSSubpopulation) newpop.subpops[j]).getFitness().fitness();
			}

			// Randomly select the parents for crossover.
			int parentIndex = selectFitness(fitnesses, state.random[threadnum].nextDouble());
			Subpopulation parent = newpop.subpops[parentIndex];
			MLSSubpopulation[] children = new MLSSubpopulation[n];


			for (int child = 0; child < n; child++) {
				boolean addIndividual = state.random[threadnum].nextBoolean();

				numIndividualsPerSubpop[index+child] = parent.individuals.length;

				if (addIndividual) {
					// Add a new individual to the subpopulation.
					children[child] = (MLSSubpopulation) parent.emptyClone();
					children[child].individuals = new Individual[parent.individuals.length+1];

					for (int ind = 0; ind < parent.individuals.length; ind++) {
						children[child].individuals[ind] = parent.individuals[ind];
					}

					children[child].individuals[parent.individuals.length] = parent.species.newIndividual(state, threadnum);

					numIndividualsPerSubpop[index+child]++;
				} else {
					// Remove an arbitrary individual from the subpopulation.
					children[child] = (MLSSubpopulation) parent.emptyClone();
					children[child].individuals = new Individual[parent.individuals.length-1];

					int indIndex = state.random[threadnum].nextInt(parent.individuals.length);

					for (int ind = 0; ind < parent.individuals.length; ind++) {
						if (ind < indIndex) {
							children[child].individuals[ind] = parent.individuals[ind];
						} else {
							children[child].individuals[ind] = parent.individuals[ind+1];
						}
					}

					numIndividualsPerSubpop[index+child]--;
				}

				// Evaluate the child subpopulation.
				((MLSEvaluator) state.evaluator).evaluateSubpopulation(state, children[child]);

				// Add the subpopulation to the population
				newpop.subpops[index+child] = children[child];
			}

			index += n;
		}

		return n;
	}

	// Updates the population provided in the reference.
	protected void breedIndividuals(EvolutionState state, Population pop) {
		// The number of threads will either be amount of threads specified,
		// or the number of individuals which need to be generated
		// (whichever one is smaller).

		int numInds = ((MLSEvolutionState) state).getTotalNumIndividuals();
		int numThreads = Math.min(numInds, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int[] numToBreed = new int[numThreads];
		int[] from = new int[numThreads];
		double[] subpopFitnesses = new double[pop.subpops.length];

		int indsPerThread = numInds / numThreads;
		int slop = numInds - numThreads * indsPerThread;
		int currentFrom = 0;

		for (int thread = 0; thread < numThreads; thread++) {
			if (slop > 0) {
				numToBreed[thread] = indsPerThread + 1;
				slop--;
			} else {
				numToBreed[thread] = indsPerThread;
			}

			if (numToBreed[thread] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}

			from[thread] = currentFrom;
			currentFrom += numToBreed[thread];
		}

		// Copy the fitnesses of the subpopulations.
		for (int subpop = 0; subpop < pop.subpops.length; subpop++) {
			subpopFitnesses[subpop] = ((MLSSubpopulation) pop.subpops[subpop]).getFitness().fitness();
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedIndChunk(pop, state, numToBreed[0], from[0], subpopFitnesses, 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				IndividualBreederThread r = new IndividualBreederThread();
				r.newpop = pop;
				r.numInds = numToBreed[i];
				r.from = from[i];
				r.fitnesses = subpopFitnesses;
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}
	}

	protected void breedIndChunk(Population newpop,
			EvolutionState state,
			int numInds,
			int from,
			final double[] subpopFitnesses,
			int threadnum) {
		// Randomly select a parent based on the fitness.
		int subpop = selectFitness(subpopFitnesses, state.random[threadnum].nextDouble());

		// Get the breeding pipeline from the subpopulation.
		BreedingPipeline bp = null;
		if (clonePipelineAndPopulation) {
			bp = (BreedingPipeline) newpop.subpops[subpop].species.pipe_prototype.clone();
		} else {
			bp = (BreedingPipeline) newpop.subpops[subpop].species.pipe_prototype;
		}

		// TODO right, I can't simply use this, since I need to add in
		// the individuals in a synchronized manner.
		// TODO this won't work!!!!!!!!!!!!!!!!!!!
		// TODO JASON!!!!!!!!!!!!!!!!!

		// Check to make sure that the breeding pipeline produces
        // the right kind of individuals.  Don't want a mistake there! :-)
        int x;
        if (!bp.produces(state,newpop,subpop,threadnum))
            state.output.fatal("The Breeding Pipeline of subpopulation " + subpop + " does not produce individuals of the expected species " + newpop.subpops[subpop].species.getClass().getName() + " or fitness " + newpop.subpops[subpop].species.f_prototype );
        bp.prepareToProduce(state,subpop,threadnum);

        // Start breeding!

        x = from;
        int upperbound = from + numInds;
        while(x<upperbound)
            x += bp.produce(1,upperbound-x,x,subpop,
                newpop.subpops[subpop].individuals,
                state,threadnum);
        if (x>upperbound) // uh oh!  Someone blew it!
            state.output.fatal("Whoa!  A breeding pipeline overwrote the space of another pipeline in subpopulation " + subpop + ".  You need to check your breeding pipeline code (in produce() ).");

        bp.finishProducing(state,subpop,threadnum);
	}

	// I might need to keep a tracker on what the latest individual has been generated. Also need to ensure
	// that adding an individual to a subpopulation is synchronised.
	protected synchronized void addIndividualToSubpop(Population newpop,
			EvolutionState state,
			int numInds,
			int from,
			int threadnum) {
		// TODO
	}

	public Population breedFinalPopulation(final EvolutionState state, final Population metaPop) {
		Population newPop = null;

		// TODO

		return newPop;
	}

	// Helper method for selecting a subpopulation out of a population based on its fitness.
	protected static int selectFitness(final double[] fitnesses,
			final double probabilities) {
		// Assume Koza, i.e., between [0,inf) by normalising it using the softmax sigmoid.
		double[] probs = new double[fitnesses.length];
		double totalFitness = 0.0;
		for (int i = 0; i < fitnesses.length; i++) {
			totalFitness += fitnesses[i];
		}

		probs[0] = fitnesses[0] / totalFitness;
		for (int i = 1; i < fitnesses.length; i++) {
			probs[i] = fitnesses[i] / totalFitness + probs[i-1];
		}

		return RandomChoice.pickFromDistribution(probs, probabilities);
	}

	private interface BreederThreadFactory {
		public Runnable createBreederThread(Population newpop,
				int numToBreed,
				int from,
				EvolutionState state,
				int threadnum,
				MLSBreeder parent);
	}

	private class SubpopBreederThreadFactory implements BreederThreadFactory {
		public Runnable createBreederThread(Population newpop,
				int numToBreed,
				int from,
				EvolutionState state,
				int threadnum,
				MLSBreeder parent) {
			SubpopBreederThread r = new SubpopBreederThread();
			r.newpop = newpop;
			r.numGroup = numToBreed;
			r.from = from;
			r.threadnum = threadnum;
			r.parent = parent;
			return r;
		}
	}

	private class SubpopBreederThread implements Runnable {
		Population newpop;
		int numGroup;
		int from;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {
			parent.breedSubpopChunk(newpop, state, numGroup, from, threadnum);
		}
	}

	private class IndividualBreederThreadFactory implements BreederThreadFactory {
		public Runnable createBreederThread(Population newpop,
				int numToBreed,
				int from,
				EvolutionState state,
				int threadnum,
				MLSBreeder parent) {
			IndividualBreederThread r = new IndividualBreederThread();
			r.newpop = newpop;
			r.numInds = numToBreed;
			r.from = from;
			r.threadnum = threadnum;
			r.parent = parent;
			return r;
		}
	}

	private class IndividualBreederThread implements Runnable {
		Population newpop;
		int numInds;
		int from;
		double[] fitnesses;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {
			parent.breedIndChunk(newpop, state, numInds, from, fitnesses, threadnum);
		}
	}

}
