package app.evolution.multilevel;

import ec.Breeder;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.util.Parameter;
import ec.util.ThreadPool;

// In multilevel selection, the subpopulations represent the groups,
// in which individuals can be a part of.
public class MLSBreeder extends Breeder {

    public static final String P_ELITE = "elite";
    public static final String P_ELITE_FRAC = "elite-fraction";
    public static final String P_REEVALUATE_ELITES = "reevaluate-elites";
    public static final String P_SEQUENTIAL_BREEDING = "sequential";
    public static final String P_CLONE_PIPELINE_AND_POPULATION = "clone-pipeline-and-population";
    public static final String P_CROSSOVER_PROB = "crossover-prob";
    public static final String P_MUTATION_PROB = "mutation-prob";

    public static final int NOT_SET = -1;

    /** An array[subpop] of the number of elites to keep for that subpopulation */
    private int[] elite;
    private double[] eliteFrac;
    private boolean[] reevaluateElites;
    private boolean sequentialBreeding;
    private boolean clonePipelineAndPopulation;
    private Population backupPopulation = null;

    // TODO temporary code.
    private double groupCrossoverRate = 0.9;
    private double groupMutationRate = 0.1;

    private ThreadPool pool = new ThreadPool();

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

	    // load elites into top of newpop
		// loadElites(state, newPop);

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

		// Load the individuals into the meta population
		loadPopulation(state, newPop);
		breedSubpopulations(state, newPop);
		breedIndividuals(state, newPop);

		return newPop;
	}

	protected void loadPopulation(EvolutionState state, Population pop) {
		// Load in the subpopulations into the meta population.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];
			pop.subpops[i] = (Subpopulation) subpop.emptyClone();

			for (int j = 0; j < subpop.individuals.length; j++) {
				pop.subpops[i].individuals[j] = (Individual) subpop.individuals[j].clone();
			}
		}
	}

	// Updates the population provided in the reference.
	protected void breedSubpopulations(EvolutionState state, Population pop) {
		// The maximum number of threads required is the
		// number of subpopulations to breed.
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

		for (int i = 0; i < numThreads; i++) {
			if (slop > 0) {
				numGroups[i] = subpopsPerThread + 1;
				slop--;
			} else {
				numGroups[i] = subpopsPerThread;
			}

			if (numGroups[i] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}

			from[i] = currentFrom;
			currentFrom += numGroups[i];
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedSubpopChunk(pop, state, numGroups[0], from[0], 0);
		} else {
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

			// TODO how will I determine the number of individuals generated at the end will be?

			if (index > upperBound) {
				state.output.fatal("TODO"); // TODO
			}
		}
	}

	private int produceSubpop(final int min,
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
			state.output.fatal("TODO"); // TODO
		}

		return total; // TODO
	}

	private int produceSubpopCrossover(final int min,
			final int max,
			final int index,
			final Population newpop,
			final EvolutionState state,
			final int threadnum) {
		int total = 1;

		// TODO

		return total;
	}

	private int produceSubpopMutation(final int min,
			final int max,
			final int index,
			final Population newpop,
			final EvolutionState state,
			final int threadnum) {
		int total = 1;

		// TODO

		return total;
	}

	// Updates the population provided in the reference.
	protected void breedIndividuals(EvolutionState state, Population pop) {
		// Use the maximum number of threads for MLS breeding,
		// as the subpopulation sizes are variable in MLS.

		int numThreads = state.breedthreads;
		// TODO
	}

	protected void breedIndChunk(Population newpop,
			EvolutionState state,
			int[] numinds,
			int[] from,
			int threadnum) {
		// TODO
	}

	public Population breedFinalPopulation(final EvolutionState state, final Population metaPop) {
		Population newPop = null;


		return null;
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

	private class IndividualBreederThread implements Runnable {
		Population newpop;
		int[] numInds;
		int[] from;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {

		}
	}

}
