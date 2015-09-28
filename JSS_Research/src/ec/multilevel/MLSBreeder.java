package ec.multilevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Breeder;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.util.Pair;
import ec.util.Parameter;
import ec.util.ThreadPool;

// In multilevel selection, the subpopulations represent the groups,
// in which individuals can be a part of.

/**
 * TODO javadoc. Write down what the MLS breeder does.
 *
 * @author parkjohn
 *
 */

// FIXME
// - Refactor the threading to use a factory sometime later down the line.
// - Make the selection of parents work with synchronization later down the line.

// TODO add a max subpopulation size to the individuals.
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

	public static final int BINARY_SEARCH_BOUNDARY = 8;

	/** An array[subpop] of the number of elites to keep for that subpopulation */
	private boolean sequentialBreeding;
	private boolean clonePipelineAndPopulation;
	private Population backupMetaPopulation = null;
	private Population backupPopulation = null;

	private double groupCrossoverRate;
	private double groupMutationRate;

	// Temporary field variable to store the number of individuals
	// in each subpopulation in the meta population.
	private int[] numIndividualsPerSubpop;
	private int numIndividuals;

	// Temporary field variables for storing fitnesses. This is used
	// during crossover and mutation operations.
	// The fitnesses are converted to probabilities in the buffer (bufferFitnesses)
	// for roulette wheel selection of subpopulation via their fitnesses, and
	// the bufferIndex denotes the number of subpopulations to choose from.
	private double[] subpopFitnesses;
	private double[] bufferFitnesses;
	private int bufferIndex;

	private Comparator<Pair<Subpopulation, Integer>> subpopComparator = new SubpopulationComparator();
	private Comparator<Pair<Individual, Integer>> individualComparator = new IndividualComparator();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		Parameter p = new Parameter(Initializer.P_POP).push(Population.P_SIZE);
		int size = state.parameters.getInt(p,null,1);  // if size is wrong, we'll let Population complain about it -- for us, we'll just make 0-sized arrays and drop out.

		sequentialBreeding = state.parameters.getBoolean(base.push(P_SEQUENTIAL_BREEDING), null, false);
		if (sequentialBreeding && (size == 1)) { // uh oh, this can't be right
			state.output.fatal("The Breeder is breeding sequentially, but you have only one population.", base.push(P_SEQUENTIAL_BREEDING));
		}

		clonePipelineAndPopulation =state.parameters.getBoolean(base.push(P_CLONE_PIPELINE_AND_POPULATION), null, true);
		if (!clonePipelineAndPopulation && (state.breedthreads > 1)) { // uh oh, this can't be right
			state.output.fatal("The Breeder is not cloning its pipeline and population, but you have more than one thread.", base.push(P_CLONE_PIPELINE_AND_POPULATION));
		}

		groupCrossoverRate = state.parameters.getDouble(base.push(P_CROSSOVER_PROB), null, 0);
		if (groupCrossoverRate < 0.0) {
			state.output.error("Group crossover rate must have a probability >= 0.0");
		}

		groupMutationRate = state.parameters.getDouble(base.push(P_MUTATION_PROB), null, 0);
		if (groupMutationRate < 0.0) {
			state.output.error("Group mutation rate must have a probability >= 0.0");
		}

		if (groupCrossoverRate + groupMutationRate == 0.0) {
			// Assign equal probabilities to crossover and mutation if both probabilities are zero.
			state.output.warning("The group probabilities have all zero probabilities -- this will be treated as a uniform distribution.  This could be an error.");
			groupCrossoverRate = 0.5;
			groupMutationRate = 0.5;
		} else if (groupCrossoverRate + groupMutationRate >= 1.0) {
			// Normalise if probabilities are greater than 1.0.
			groupCrossoverRate = groupCrossoverRate / (groupCrossoverRate + groupMutationRate);
			groupMutationRate = groupMutationRate / (groupCrossoverRate + groupMutationRate);
		}

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
		Population initPop = ((MLSEvolutionState) state).getMetaPopulation();

		Population metaPop = breedMetaPopulation(state, initPop);
		Population finalPop = breedFinalPopulation(state, metaPop);

		return finalPop;
	}

	/**
	 * Returns true if we're doing sequential breeding.
	 */
	public boolean isSequentialBreeding() {
		return sequentialBreeding;
	}

	/**
	 * Returns true if we're doing sequential breeding and it's the subpopulation's turn (round robin, one subpopulation per generation).
	 */
	public boolean shouldBreedSubpop(EvolutionState state, int subpop, int threadnum) {
		return !sequentialBreeding || (state.generation % state.population.subpops.length) == subpop;
	}

	/**
	 * Breeds the subpopulations and individuals for the meta-population.
	 *
	 * A template which contains the information for the number of subpopulations
	 * to breed is passed into this method as a parameter.
	 *
	 * @return A meta-population cloned from the template, then populated with
	 * subpopulations and individuals from the population in EvolutionState.
	 */
	public Population breedMetaPopulation(final EvolutionState state, Population metaPop) {
		Population newPop = null;

		if (clonePipelineAndPopulation) {
			newPop = (Population) metaPop.emptyClone();
		} else {
			if (backupMetaPopulation == null) {
				backupMetaPopulation = (Population) metaPop.emptyClone();
			}
			newPop = backupMetaPopulation;
			newPop.clear();
			backupMetaPopulation = (Population) metaPop;
		}

		numIndividualsPerSubpop = new int[newPop.subpops.length];
		numIndividuals = 0;

		subpopFitnesses = new double[newPop.subpops.length];
		bufferFitnesses = new double[newPop.subpops.length];
		bufferIndex = state.population.subpops.length;

		// Load the individuals into the meta population
		loadPopulation(state, newPop);
		breedSubpopulations(state, newPop);
		breedIndividuals(state, newPop);

		return newPop;
	}

	/**
	 * Load in the original subpopulations of individuals from the EvolutionState
	 * into the Population passed in as parameter.
	 */
	protected void loadPopulation(EvolutionState state, Population metaPop) {
		int buffer = ((MLSEvolutionState) state).getTotalNumIndividuals();

		// Load in the subpopulations into the meta population.
		for (int s = 0; s < state.population.subpops.length; s++) {
			Subpopulation subpop = state.population.subpops[s];

			metaPop.subpops[s] = (Subpopulation) subpop.emptyClone();
			metaPop.subpops[s].individuals = new Individual[subpop.individuals.length + buffer];

			numIndividualsPerSubpop[s] = subpop.individuals.length;
			numIndividuals += numIndividualsPerSubpop[s];

			for (int i = 0; i < subpop.individuals.length; i++) {
				metaPop.subpops[s].individuals[i] = (Individual) subpop.individuals[i].clone();
			}

			// Load in the fitnesses of the subpopulations.
			subpopFitnesses[s] = ((MLSSubpopulation) subpop).getFitness().fitness();
			bufferFitnesses[s] = ((MLSSubpopulation) subpop).getFitness().fitness();
		}
	}

	/**
	 * Breed the subpopulations to fill out the vacant slots in the meta-population.
	 * The Population provided in the parameter and is updated by reference.
	 */
	protected void breedSubpopulations(EvolutionState state, Population metaPop) {
		// The maximum number of threads required is the number of subpopulations to breed.
		int subpopDiff = metaPop.subpops.length - state.population.subpops.length;
		int numThreads = Math.min(subpopDiff, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int numGroups[] = new int[numThreads];
		int from[] = new int[numThreads];

		int subpopsPerThread = subpopDiff / numThreads;
		int slop = subpopDiff - numThreads * subpopsPerThread;
		int currentFrom = state.population.subpops.length;

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
			breedSubpopChunk(metaPop, state, numGroups[0], from[0], 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				SubpopBreederThread r = new SubpopBreederThread();
				r.newpop = metaPop;
				r.numGroup = numGroups[i];
				r.from = from[i];
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}

		// Load the population into the temporary population in evolution state.
		((MLSEvolutionState) state).setTempPopulation(metaPop);
	}

	/**
	 * Breed the subpopulations in the chunk of the population provided.
	 */
	protected void breedSubpopChunk(Population newPop,
			EvolutionState state,
			int numGroup,
			int from,
			int threadnum) {
		int index = from;
		int upperBound = from + numGroup;
		while (index < upperBound) {
			index += produceSubpop(1, upperBound - index, index, newPop, state, threadnum);

			if (index > upperBound) {
				state.output.fatal("A breeding pipeline overwrote the space of another pipeline in the population.  You need to check your breeding pipeline code (in produce() ).");
			}
		}
	}

	// FIXME The breeding of the subpopulations is hard coded into the breeder.
	// This will need to be fixed sometime later down the line.
	private int produceSubpop(final int min,
			final int max,
			final int index,
			final Population newPop,
			final EvolutionState state,
			final int threadnum) {
		double value = state.random[threadnum].nextDouble();
		int total = 0;

		if (value < groupCrossoverRate) {
			total = produceSubpopCrossover(min, max, index, newPop, state, threadnum);
		} else if (value < groupCrossoverRate + groupMutationRate) { // Should always happen for the current code.
			total = produceSubpopMutation(min, max, index, newPop, state, threadnum);
		} else {
			state.output.fatal("The possible methods of producing subpopulations do not sufficiently cover for the random value.");
		}

		return total;
	}

	private int produceSubpopCrossover(final int min,
			final int max,
			final int start,
			final Population newPop,
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
			int[] parentIndices = getCrossoverParents(state, threadnum, bufferIndex);
			Subpopulation[] parents = new Subpopulation[] { newPop.subpops[parentIndices[0]], newPop.subpops[parentIndices[1]] };

			// Arbitrarily select the individuals to exchange in between the two subpopulations.
			int[] swapIndices = new int[]{
					state.random[threadnum].nextInt(numIndividualsPerSubpop[parentIndices[0]]),
					state.random[threadnum].nextInt(numIndividualsPerSubpop[parentIndices[1]])
			};
			int length = CROSSOVER_PARENTS_REQUIRED;

			// Only generate as many children as required.
			for (int child = 0; child < n; child++) {
				// Populate the child subpopulation with individuals.
				MLSSubpopulation childSubpop = (MLSSubpopulation) parents[child%length].emptyClone();

				int parentLength = numIndividualsPerSubpop[parentIndices[child%length]];

				for (int ind = 0; ind < parentLength; ind++) {
					if (ind == swapIndices[child]) {
						childSubpop.individuals[ind] = parents[(child+1)%length].individuals[swapIndices[(child+1)%length]];
					} else {
						childSubpop.individuals[ind] = parents[child%length].individuals[ind];
					}
				}

				// Evaluate the child subpopulation.
				((MLSEvaluator) state.evaluator).evaluateSubpopulation(state, childSubpop, index+child);

				// Add the subpopulation to the population
				newPop.subpops[index+child] = childSubpop;

				numIndividualsPerSubpop[index+child] = parentLength;
				numIndividuals += numIndividualsPerSubpop[index+child];

				subpopFitnesses[index+child] = ((MLSSubpopulation) childSubpop).getFitness().fitness();
				bufferIndex++;
			}

			index += n;
		}

		return n;
	}

	private int[] getCrossoverParents(EvolutionState state, int threadnum, int length) {
		int parentIndex1 = selectFitness(subpopFitnesses, bufferFitnesses, length, state.random[threadnum].nextDouble());

		if (parentIndex1 != length - 1) {
			// Swap the fitnesses.
			double temp = subpopFitnesses[parentIndex1];
			subpopFitnesses[parentIndex1] = subpopFitnesses[length-1];
			subpopFitnesses[length-1] = temp;
		}

		int parentIndex2 = selectFitness(subpopFitnesses, bufferFitnesses, length-1, state.random[threadnum].nextDouble());

		if (parentIndex1 == parentIndex2) {
			parentIndex2 = length-1;
		}

		if (parentIndex1 != length - 1) {
			// Swap back the fitnesses.
			double temp = subpopFitnesses[parentIndex1];
			subpopFitnesses[parentIndex1] = subpopFitnesses[length-1];
			subpopFitnesses[length-1] = temp;
		}

		return new int[]{parentIndex1, parentIndex2};
	}

	private int produceSubpopMutation(final int min,
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
			int parentIndex = getMutationParent(state, threadnum, bufferIndex);

			Subpopulation parent = newpop.subpops[parentIndex];
			MLSSubpopulation[] children = new MLSSubpopulation[n];


			for (int child = 0; child < n; child++) {
				boolean addIndividual = state.random[threadnum].nextBoolean();

				numIndividualsPerSubpop[index+child] = numIndividualsPerSubpop[parentIndex];

				// FIXME hack to force adding individuals if there's only one individual in the parent subpopulation.
				if (addIndividual || numIndividualsPerSubpop[index+child] == 1) {
					// Add a new individual to the subpopulation.
					children[child] = (MLSSubpopulation) parent.emptyClone();
					children[child].individuals = new Individual[parent.individuals.length+1];

					for (int ind = 0; ind < numIndividualsPerSubpop[parentIndex]; ind++) {
						children[child].individuals[ind] = parent.individuals[ind];
					}

					children[child].individuals[numIndividualsPerSubpop[parentIndex]] = parent.species.newIndividual(state, threadnum);

					numIndividualsPerSubpop[index+child]++;
				} else {
					// Remove an arbitrary individual from the subpopulation.
					children[child] = (MLSSubpopulation) parent.emptyClone();
					children[child].individuals = new Individual[parent.individuals.length-1];

					int indIndex = state.random[threadnum].nextInt(numIndividualsPerSubpop[parentIndex]);

					for (int ind = 0; ind < numIndividualsPerSubpop[parentIndex]; ind++) {
						if (ind < indIndex) {
							children[child].individuals[ind] = parent.individuals[ind];
						} else {
							children[child].individuals[ind] = parent.individuals[ind+1];
						}
					}

					numIndividualsPerSubpop[index+child]--;
				}

				// Evaluate the child subpopulation.
				((MLSEvaluator) state.evaluator).evaluateSubpopulation(state, children[child], index+child);

				// Add the subpopulation to the population
				newpop.subpops[index+child] = children[child];

				numIndividuals += numIndividualsPerSubpop[index+child];

				subpopFitnesses[index+child] = ((MLSSubpopulation) children[child]).getFitness().fitness();
				bufferIndex++;
			}

			index += n;
		}

		return n;
	}

	private int getMutationParent(EvolutionState state, int threadnum, int length) {
		return selectFitness(subpopFitnesses, bufferFitnesses, length, state.random[threadnum].nextDouble());
	}

	/**
	 * Breed the individuals to fill out the vacant slots in the individuals array in
	 * the subpopulations of the meta-population. The meta-population is the Population
	 * provided in the parameter and is updated by reference.
	 */
	protected void breedIndividuals(EvolutionState state, Population newPop) {
		// The number of threads will either be amount of threads specified,
		// or the number of individuals which need to be generated
		// (whichever one is smaller).
		int numToBreed = ((MLSEvolutionState) state).getTotalNumIndividuals();
		int numThreads = Math.min(numToBreed, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int numInds[] = new int[numThreads];
		int from[][] = new int[numThreads][newPop.subpops.length];
		BreedingPipeline[] breedingPipelines = new BreedingPipeline[newPop.subpops.length];

		// We will have some extra individuals.  We distribute these among the early subpopulations.
		int subpopsPerThread = numToBreed / numThreads;  // integer division
		int slop = numToBreed - numThreads * subpopsPerThread;

		// Find the number of individuals to generate per thread.
		for (int thread = 0; thread < numThreads; thread++) {
			if (slop > 0) {
				numInds[thread] = subpopsPerThread + 1;
				slop--;
			} else {
				numInds[thread] = subpopsPerThread;
			}

			if (numInds[thread] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}
		}

		List<Pair<Double, Integer>> selectableSubpops = new ArrayList<Pair<Double, Integer>>();

		for (int s = 0; s < newPop.subpops.length; s++) {
			MLSSubpopulation subpop = (MLSSubpopulation) newPop.subpops[s];

			// Subpopulation needs to have less than double the initial
			// number of individuals to be considered for selection.
			if (numIndividualsPerSubpop[s] < 2 * ((MLSEvolutionState) state).getInitSubpopSize()) {
				selectableSubpops.add(new Pair<Double, Integer>(subpop.getFitness().fitness(), s));
			}

			// Copy the breeding pipeline of the subpopulations.
			breedingPipelines[s] = (BreedingPipeline) ((clonePipelineAndPopulation) ?
					subpop.species.pipe_prototype.clone() :
						subpop.species.pipe_prototype);

			// Find the starting points for each subpopulation.
			from[0][s] = numIndividualsPerSubpop[s];
			for (int thread = 1; thread < numThreads; thread++) {
				from[thread][s] = from[thread-1][s] + numInds[thread];
			}
		}

		// Setup the arrays required for subpopulation fitnesses.
		double[] subpopFits = new double[selectableSubpops.size()];
		double[] buffer = new double[selectableSubpops.size()];
		int[] subpopIndices = new int[selectableSubpops.size()];

		for (int i = 0; i < selectableSubpops.size(); i++) {
			subpopFits[i] = selectableSubpops.get(i).i1;
			subpopIndices[i] = selectableSubpops.get(i).i2;
		}

		// Setup all of the individual breeding pipelines.
		for (int thread = 0; thread < numThreads; thread++) {
			for (int subpop = 0; subpop < newPop.subpops.length; subpop++) {
				// Check to make sure that the breeding pipeline produces
				// the right kind of individuals.  Don't want a mistake there! :-)
				if (!breedingPipelines[subpop].produces(state, newPop, subpop, thread)) {
					state.output.fatal("The Breeding Pipeline of subpopulation " + bufferIndex + " does not produce individuals of the expected species " + newPop.subpops[subpop].species.getClass().getName() + " or fitness " + newPop.subpops[subpop].species.f_prototype );
				}
				breedingPipelines[subpop].prepareToProduce(state, subpop, thread);
			}
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedIndChunk(newPop, state, numInds[0], from[0], subpopFits, buffer, subpopIndices, breedingPipelines, 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				IndividualBreederThread r = new IndividualBreederThread();
				r.newpop = newPop;
				r.numInds = numInds[i];
				r.from = from[i];
				r.subpopFits = subpopFits;
				r.buffer = buffer;
				r.subpopIndices = subpopIndices;
				r.breedingPipelines = breedingPipelines;
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}

		// Close all of the individual breeding pipelines.
		for (int thread = 0; thread < numThreads; thread++) {
			for (int subpop = 0; subpop < newPop.subpops.length; subpop++) {
				breedingPipelines[subpop].finishProducing(state, subpop, thread);
			}
		}

		// Trim the individuals array.
		for (int subpop = 0; subpop < newPop.subpops.length; subpop++) {
			Individual[] curInds = newPop.subpops[subpop].individuals;
			Individual[] trimInds = new Individual[numIndividualsPerSubpop[subpop]];

			int curIndex = 0;
			int trimIndex = 0;
			while (curIndex < curInds.length && trimIndex < trimInds.length) {
				if (curInds[curIndex] != null) {
					trimInds[trimIndex++] = curInds[curIndex];
				}

				curIndex++;
			}

			newPop.subpops[subpop].individuals = trimInds;
		}
	}

	/**
	 * Breed the individuals in the chunk of the subpopulation provided.
	 */
	protected void breedIndChunk(Population newPop,
			EvolutionState state,
			int numInds,
			int[] from,
			double[] subpopFits,
			double[] buffer,
			int[] subpopIndices,
			final BreedingPipeline[] breedingPipelines,
			int threadnum) {
		int totalNumBred = 0;

		while(totalNumBred < numInds) {
			// Randomly select a parent based on the fitness.
			int pseudoIndex = selectFitness(subpopFits, buffer, subpopFits.length, state.random[threadnum].nextDouble());
			int subpopIndex = subpopIndices[pseudoIndex];

			MLSSubpopulation subpop = (MLSSubpopulation) newPop.subpops[subpopIndex];

			// Start breeding!
			int index = from[subpopIndex] + totalNumBred;
			int upperBound = from[subpopIndex] + numInds;

			int numBred = breedingPipelines[subpopIndex].produce(1,
					upperBound - index,
					index,
					subpopIndex,
					subpop.individuals,
					state,
					threadnum);
			numIndividualsPerSubpop[subpopIndex] += numBred;
			numIndividuals += numBred;

			// Evaluate the offspring individuals as they are being generated.
			for (int i = index; i < index + numBred; i++) {
				if (subpop.individuals[i] == null) {
					state.output.fatal("The individuals were not breed at the subpopulation " + subpopIndex + ".");
				}

				((MLSEvaluator) state.evaluator).evaluateIndividual(state, subpop, subpopIndex, subpop.individuals[i]);
			}

			totalNumBred += numBred;
		}

		// Safety check to ensure that you don't override another section of the pipeline.
		if (totalNumBred > numInds) {
			state.output.fatal("A breeding pipeline overwrote the space of another pipeline.  You need to check your breeding pipeline code (in produce() ).");
		}
	}

	/**
	 * Breeds the subpopulations and individuals for the final population from the
	 * meta-population provided.
	 *
	 * @return The final-population cloned from the population in EvolutionState,
	 * then populated by the elite subpopulations and individuals from the meta-
	 * population.
	 */
	public Population breedFinalPopulation(final EvolutionState state, final Population metaPop) {
		Population newPop = null;

		if (clonePipelineAndPopulation) {
			newPop = (Population) state.population.emptyClone();
		} else {
			if (backupPopulation == null) {
				backupPopulation = (Population) state.population.emptyClone();
			}
			newPop = backupPopulation;
			newPop.clear();
			backupPopulation = state.population;
		}

		// Keep the best individuals and the best groups.
		loadElites(state, newPop, metaPop);

		return newPop;
	}

	/**
	 * Filters out the elite subpopulations and individuals from the meta-population
	 * and inserts them into the final population.
	 */
	protected void loadElites(EvolutionState state, Population newPop, Population metaPop) {
		// Sort and load in the best groups.
		List<Pair<Subpopulation, Integer>> metaSubpops = new ArrayList<Pair<Subpopulation, Integer>>();
		for (int s = 0; s < metaPop.subpops.length; s++) {
			metaSubpops.add(new Pair<Subpopulation, Integer>(metaPop.subpops[s], s));
		}

		Collections.sort(metaSubpops, subpopComparator);

		List<Pair<Individual, Integer>> bestGroupInds = new ArrayList<Pair<Individual, Integer>>(numIndividuals);

		// Load elite will copy over the individual subpopulations.
		newPop.subpops = new Subpopulation[((MLSEvolutionState) state).getInitNumSubpops()];

		// Retain as many subpopulations as the number of subpopulations initially generated.
		for (int s = 0; s < ((MLSEvolutionState) state).getInitNumSubpops(); s++) {
			Subpopulation subpop = metaSubpops.get(s).i1;
			newPop.subpops[s] = subpop;

			for (int i = 0; i < subpop.individuals.length; i++) {
				if (subpop.individuals[i] == null) { continue; }

				Pair<Individual, Integer> d = new Pair<Individual, Integer>(subpop.individuals[i], s);
				bestGroupInds.add(d);
			}
		}

		// Find and temporary store the elite individuals in the population.
		Collections.sort(bestGroupInds, individualComparator);

		Map<Integer, List<Individual>> subpopMap = new HashMap<Integer, List<Individual>>();

		System.out.println("Number of individuals generated: " + numIndividuals);
		System.out.println("Number of individuals to keep: " + ((MLSEvolutionState) state).getTotalNumIndividuals());

		for (int i = 0; i < ((MLSEvolutionState) state).getTotalNumIndividuals(); i++) {
			Pair<Individual, Integer> d = bestGroupInds.get(i);

			if (!subpopMap.containsKey(d.i2)) {
				subpopMap.put(d.i2, new ArrayList<Individual>());
			}

			subpopMap.get(d.i2).add(d.i1);
		}

		// Upload the elite indviduals to the state, removing any empty subpopulations.
		boolean[] subpopIsEmpty = new boolean[newPop.subpops.length];
		boolean anySubpopIsEmpty = false;

		List<Subpopulation> nonEmptySubpops = new ArrayList<Subpopulation>();

		for (int s = 0; s < newPop.subpops.length; s++) {
			List<Individual> inds = subpopMap.get(s);

			if (inds != null) {
				newPop.subpops[s].individuals = new Individual[inds.size()];

				for (int i = 0; i < inds.size(); i++) {
					newPop.subpops[s].individuals[i] = inds.get(i);
				}

				subpopIsEmpty[s] = false;
				nonEmptySubpops.add(newPop.subpops[s]);
			} else {
				subpopIsEmpty[s] = true;
				anySubpopIsEmpty = true;
			}
		}

		// If there is a case where subpopulation is empty due to individual filtering,
		// then repopulate the subpopulation with new individuals: half from "good"
		// subpopulations, other half randomly.
		if (anySubpopIsEmpty) {
			for (int s = 0; s < newPop.subpops.length; s++) {
				if (subpopIsEmpty[s]) {
					repopulateSubpopulation(state, nonEmptySubpops, newPop.subpops[s]);
				}
			}
		}
	}

	private void repopulateSubpopulation(EvolutionState state, List<Subpopulation> nonEmptySubpops, Subpopulation emptySubpop) {
		System.out.println("Repopulating subpopulation:");

		int initSubpopSize = ((MLSEvolutionState) state).getInitSubpopSize();

		emptySubpop.individuals = new Individual[initSubpopSize];
		for (int i = 0; i < initSubpopSize; i++) {
			// Select a subpopulation to sample from.
			double[] fitnesses = new double[nonEmptySubpops.size()];
			double[] buffer = new double[nonEmptySubpops.size()];

			for (int s = 0; s < nonEmptySubpops.size(); s++) {
				fitnesses[s] = ((MLSSubpopulation) nonEmptySubpops.get(s)).getFitness().fitness();
			}

			Subpopulation subpop = nonEmptySubpops.get(selectFitness(fitnesses, buffer, nonEmptySubpops.size(), state.random[0].nextDouble()));

			// Select an individual from the subpopulation.
			Individual ind = subpop.individuals[state.random[0].nextInt(subpop.individuals.length)];
			emptySubpop.individuals[i] = (Individual) ind.clone();
		}
	}

	// Helper method for selecting a subpopulation out of a population based on its fitness.
	protected static final int selectFitness(double[] fitnesses, double[] buffer, int length, double prob) {
		// Quick pre-check.
		if (length == 1) {
			return 0;
		}

		// Load in the fitnesses into the buffer.
		buffer[0] = fitnesses[0];
		for (int i = 1; i < length; i++) {
			buffer[i] = buffer[i-1] + fitnesses[i];
		}

		// Normalise the buffer to probability values.
		for (int i = 0; i < length; i++) {
			buffer[i] /= buffer[length-1];
		}

		if (length < BINARY_SEARCH_BOUNDARY) {
			// Carry out a linear search.
			for (int i = 0; i < length-1; i++) {
				if (buffer[i] > prob) {
					return exemptZeroes(fitnesses, i);
				}
			}
			return exemptZeroes(fitnesses, length-1);
		} else {
			// Carry out the binary search.
			int top = length - 1;
			int bottom = 0;
			int cur;

			while (top != bottom) {
                cur = (top + bottom) / 2; // integer division

                if (buffer[cur] > prob) {
                    if (cur == 0 || buffer[cur-1] <= prob) {
                        return exemptZeroes(fitnesses, cur);
                    } else { // step down
                        top = cur;
                    }
                } else if (cur == buffer.length-1) { // oops
                    return exemptZeroes(fitnesses, cur);
                } else if (bottom == cur) { // step up
                    bottom++;  // (8 + 9)/2 = 8
                } else {
                    bottom = cur;  // (8 + 10) / 2 = 9
                }
			}
			return exemptZeroes(fitnesses, bottom);
		}
	}

	private static final int exemptZeroes(double[] fitnesses, int index) {
	    if (fitnesses[index] == 0.0) { // I need to scan forward because I'm in a left-trail
	        // scan forward
	        while (index < fitnesses.length-1 && fitnesses[index] == 0.0) { index++; }
	    } else {
	        // scan backwards
	        while (index > 0 && fitnesses[index] == fitnesses[index-1]) { index--; }
	    }
	    return index;
    }

	// Threads for breeding subpopulations.
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

	// Thread for breeding individuals.
	private class IndividualBreederThread implements Runnable {
		Population newpop;
		int numInds;
		int[] from;
		double[] subpopFits;
		double[] buffer;
		int[] subpopIndices;
		BreedingPipeline[] breedingPipelines;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {
			parent.breedIndChunk(newpop, state, numInds, from, subpopFits, buffer, subpopIndices, breedingPipelines, threadnum);
		}
	}

	// Compares the fitnesses of the subpopulations.
	private class SubpopulationComparator implements Comparator<Pair<Subpopulation, Integer>> {
		public int compare(Pair<Subpopulation, Integer> s1, Pair<Subpopulation, Integer> s2) {
			if (((MLSSubpopulation) s1.i1).getFitness().betterThan(((MLSSubpopulation) s2.i1).getFitness())) {
				return -1;
			} else if (((MLSSubpopulation) s2.i1).getFitness().betterThan(((MLSSubpopulation) s1.i1).getFitness())) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	// Compares the fitnesses of the individuals.
	private class IndividualComparator implements Comparator<Pair<Individual, Integer>> {
		public int compare(Pair<Individual, Integer> d1, Pair<Individual, Integer> d2) {
			if (d1.i1.fitness.betterThan(d2.i1.fitness)) {
				return -1;
			} else if (d1.i1.fitness.betterThan(d2.i1.fitness)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
