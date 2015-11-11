package ec.multilevel_new;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ec.Breeder;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.util.MersenneTwisterFast;
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
public class MLSBreeder extends Breeder {

	private static final long serialVersionUID = -6914152113435773281L;

	public static final String P_ELITE = "elite";
	public static final String P_ELITE_FRAC = "elite-fraction";
	public static final String P_REEVALUATE_ELITES = "reevaluate-elites";
	public static final String P_SEQUENTIAL_BREEDING = "sequential";
	public static final String P_CLONE_PIPELINE_AND_POPULATION = "clone-pipeline-and-population";

	public static final String P_NUM_GROUP_BREED = "num-breed";
	public static final String P_NUM_GROUP_RETAIN = "num-retain";

	public static final String P_COOPERATION_PROB = "cooperation-prob"; // FIXME remove these parameters when I make breeding more generic.
	public static final String P_CROSSOVER_PROB = "crossover-prob";
	public static final String P_MUTATION_PROB = "mutation-prob";

	public static final String P_BIAS_FACTOR = "bias-factor";

	public static final int NOT_SET = -1;

	public static final int COOPERATION_PARENTS_REQUIRED = 2;
	public static final int CROSSOVER_PARENTS_REQUIRED = 2;
	public static final int MUTATION_PARENTS_REQUIRED = 1;
	public static final int COOPERATION_INDS_PRODUCED = 1;
	public static final int CROSSOVER_INDS_PRODUCED = 2;
	public static final int MUTATION_INDS_PRODUCED = 1;

	public static final int BINARY_SEARCH_BOUNDARY = 8;

	/** An array[subpop] of the number of elites to keep for that subpopulation */
	private boolean sequentialBreeding;
	private boolean clonePipelineAndPopulation;
	private Population backupMetaPopulation = null;
	private Population backupPopulation = null;

	private int numGroupBreed = NOT_SET;
	private int numGroupRetain = NOT_SET;

	private double groupCooperationRate;
	private double groupCrossoverRate;
	private double groupMutationRate;

	private double biasFactor;

	private MLSCoopPopulation coopPopulation;

	private Comparator<Pair<Subpopulation, Integer>> groupComparator = new GroupComparator();
	private Comparator<Pair<Individual, Integer>> individualComparator = new IndividualComparator();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		Parameter p = new Parameter(Initializer.P_POP).push(Population.P_SIZE);
		int size = state.parameters.getInt(p, null, 1);  // if size is wrong, we'll let Population complain about it -- for us, we'll just make 0-sized arrays and drop out.

		sequentialBreeding = state.parameters.getBoolean(base.push(P_SEQUENTIAL_BREEDING), null, false);
		if (sequentialBreeding && (size == 1)) { // uh oh, this can't be right
			state.output.fatal("The Breeder is breeding sequentially, but you have only one population.", base.push(P_SEQUENTIAL_BREEDING));
		}

		clonePipelineAndPopulation = state.parameters.getBoolean(base.push(P_CLONE_PIPELINE_AND_POPULATION), null, true);
		if (!clonePipelineAndPopulation && (state.breedthreads > 1)) { // uh oh, this can't be right
			state.output.fatal("The Breeder is not cloning its pipeline and population, but you have more than one thread.", base.push(P_CLONE_PIPELINE_AND_POPULATION));
		}

		numGroupBreed = state.parameters.getIntWithDefault(base.push(P_NUM_GROUP_BREED), null, NOT_SET);
		if (numGroupBreed == NOT_SET) {
			state.output.fatal("Need to set the number of group to breed");
		}

		numGroupRetain = state.parameters.getIntWithDefault(base.push(P_NUM_GROUP_RETAIN), null, NOT_SET);
		if (numGroupRetain == NOT_SET) {
			state.output.fatal("Need to set the number of group to breed");
		}

		groupCooperationRate = state.parameters.getDouble(base.push(P_COOPERATION_PROB), null, 0);
		if (groupCooperationRate < 0.0) {
			state.output.error("Group cooperation rate must have a probability >= 0.0. Defaulting to 0.0");
			groupCooperationRate = 0.0;
		}

		groupCrossoverRate = state.parameters.getDouble(base.push(P_CROSSOVER_PROB), null, 0);
		if (groupCrossoverRate < 0.0) {
			state.output.error("Group crossover rate must have a probability >= 0.0. Defaulting to 0.0");
			groupCrossoverRate = 0.0;
		}

		groupMutationRate = state.parameters.getDouble(base.push(P_MUTATION_PROB), null, 0);
		if (groupMutationRate < 0.0) {
			state.output.error("Group mutation rate must have a probability >= 0.0. Defaulting to 0.0");
			groupMutationRate = 0.0;

		}

		biasFactor = state.parameters.getDouble(base.push(P_BIAS_FACTOR), null, 1.0);

		if (groupCooperationRate + groupCrossoverRate + groupMutationRate == 0.0) {
			// Assign equal probabilities to crossover and mutation if both probabilities are zero.
			state.output.warning("The group probabilities have all zero probabilities -- this will be treated as a uniform distribution.  This could be an error.");
			groupCooperationRate = 1.0 / 3.0;
			groupCrossoverRate = 1.0 / 3.0;
			groupMutationRate = 1.0 / 3.0;
		} else if (groupCooperationRate + groupCrossoverRate + groupMutationRate >= 1.0) {
			// Normalise if probabilities are greater than 1.0.
			double sum = groupCooperationRate + groupCrossoverRate + groupMutationRate;

			groupCooperationRate = 1.0 * groupCooperationRate / sum;
			groupCrossoverRate = 1.0 * groupCrossoverRate / sum;
			groupMutationRate = 1.0 * groupMutationRate / sum;
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
		MLSEvolutionState mlsState = (MLSEvolutionState) state;

		mlsState.initialiseMetaPopulation(numGroupBreed);

		Population initPop = mlsState.getMetaPopulation();

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
		// Do not breed subpopulation 0, as it is the non-group subpopulation that stores all the individuals.
		if (subpop == 0) {
			return false;
		}

		return !sequentialBreeding || (state.generation % state.population.subpops.length) == subpop;
	}

	/**
	 * Returns the number of groups that will be breed.
	 */
	public int getNumGroupBreed() {
		return numGroupBreed;
	}

	/**
	 * Returns the number of groups to retain.
	 */
	public int getNumGroupRetain() {
		return numGroupRetain;
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

		// The numbers of groups and individuals that will be generated.
		int numMetaGroups = newPop.subpops.length - 1;
		int numMetaInds = 2 * state.population.subpops[0].individuals.length;

		newPop.subpops[0].individuals = new Individual[numMetaInds];

		coopPopulation = new MLSCoopPopulation(numMetaGroups, numMetaInds);

		// Load the individuals into the meta population
		loadPopulation(state, newPop);
		breedGroups(state, newPop);
		breedIndividuals(state, newPop);



		return newPop;
	}

	/**
	 * Load in the original subpopulations of individuals from the EvolutionState
	 * into the Population passed in as parameter.
	 */
	protected void loadPopulation(EvolutionState state, Population newPop) {
		// Copy the entire population stored in the first subpopulation.
		Subpopulation overallSubpop = state.population.subpops[0];
		for (int i = 0; i < overallSubpop.individuals.length; i++) {
			newPop.subpops[0].individuals[i] = overallSubpop.individuals[i];

			coopPopulation.addIndividual((MLSGPIndividual) overallSubpop.individuals[i]);
		}

		// Copy the groups represented by subsequent subpopulations.
		for (int i = 1; i < state.population.subpops.length; i++) {
			newPop.subpops[i] = state.population.subpops[i];

			coopPopulation.addGroup((MLSSubpopulation) state.population.subpops[i]);
		}

		// Load the population of cooperative entities into the state.
		((MLSEvolutionState) state).setCoopPopulation(coopPopulation);
	}

	/**
	 * Breed the subpopulations to fill out the vacant slots in the meta-population.
	 * The Population provided in the parameter and is updated by reference.
	 */
	protected void breedGroups(EvolutionState state, Population newPop) {
		// The maximum number of threads required is the number of subpopulations to breed.
		int numThreads = Math.min(numGroupBreed, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int numGroups[] = new int[numThreads];
		int from[] = new int[numThreads];

		int groupsPerThread = numGroupBreed / numThreads;
		int slop = numGroupBreed - numThreads * groupsPerThread;
		int currentFrom = state.population.subpops.length;

		for (int thread = 0; thread < numThreads; thread++) {
			if (slop > 0) {
				numGroups[thread] = groupsPerThread + 1;
				slop--;
			} else {
				numGroups[thread] = groupsPerThread;
			}

			if (numGroups[thread] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}

			from[thread] = currentFrom;
			currentFrom += numGroups[thread];
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedSubpopChunk(newPop, state, numGroups[0], from[0], 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				SubpopBreederThread r = new SubpopBreederThread();
				r.newPop = newPop;
				r.numGroup = numGroups[i];
				r.from = from[i];
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}
	}

	/**
	 * Breed the subpopulations in the chunk of the population provided.
	 */
	protected void breedSubpopChunk(Population newPop,
			final EvolutionState state,
			final int numGroup,
			final int from,
			final int threadnum) {
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

		MLSCoopPopulation coopPop = ((MLSEvolutionState) state).getCoopPopulation();

		if (value < groupCooperationRate || coopPop.getNumGroups() == 0) {
			total = produceSubpopCooperation(min, max, index, newPop, state, threadnum);
		} else if (value < groupCooperationRate + groupMutationRate || coopPop.getNumGroups() == 1) {
			total = produceSubpopMutation(min, max, index, newPop, state, threadnum);
		} else if (value < groupCooperationRate + groupMutationRate + groupCrossoverRate) { // Should always happen for the current code.
			total = produceSubpopCrossover(min, max, index, newPop, state, threadnum);
		} else {
			state.output.fatal("The possible methods of producing subpopulations do not sufficiently cover for the random value.");
		}

		return total;
	}

	private int produceSubpopCooperation(final int min,
			final int max,
			final int start,
			final Population newPop,
			final EvolutionState state,
			final int threadnum) {
		if (coopPopulation.getNumEntities() < COOPERATION_PARENTS_REQUIRED) {
			state.output.fatal("There are insufficient number of cooperative entities to generate new groups. Number of entities: " + coopPopulation.getNumEntities() + ", entities required: " + CROSSOVER_PARENTS_REQUIRED);
		}

		int n = COOPERATION_INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		int index = start;
		while (index < n + start) {
			int[] parentIndices = getCooperativeParentsRouletteWheel(state, threadnum);
			IMLSCoopEntity[] parents = new IMLSCoopEntity[] {
					coopPopulation.getAllEntities()[parentIndices[0]],
					coopPopulation.getAllEntities()[parentIndices[1]]
			};

			// Generate a child from the two cooperative entities selected.
			MLSSubpopulation child = (MLSSubpopulation) parents[0].combine(state, parents[1]);

			if (child.individuals.length == 0) {
				state.output.fatal("Error happening in crossover");
			}

			// Evaluate the newly generated group.
			((MLSEvaluator) state.evaluator).evaluateGroup(state, child, index + 1);

			// Add the group to the meta population and the list of entities.
			newPop.subpops[index] = child;

			coopPopulation.addGroup(child);

			index++;
		}

		return n;
	}

	private int[] getCooperativeParentsRouletteWheel(final EvolutionState state, final int threadnum) {
		int length = coopPopulation.getNumEntities();

		double[] copy = new double[length];
		for (int i = 0; i < length; i++) {
			copy[i] = coopPopulation.getAllEntities()[i].getFitness().fitness();
		}

		int parentIndex1 = rouletteSelect(copy, biasFactor, length, state.random[threadnum]);
		int parentIndex2 = rouletteSelect(copy, biasFactor, length, state.random[threadnum]);

		return new int[]{parentIndex1, parentIndex2};
	}

	private int produceSubpopCrossover(final int min,
			final int max,
			final int start,
			final Population newPop,
			final EvolutionState state,
			final int threadnum) {
		if (coopPopulation.getNumGroups() < CROSSOVER_PARENTS_REQUIRED) {
			state.output.fatal("There are insufficient number of groups to generate new groups. Number of groups: " + start + ", groups required: " + CROSSOVER_PARENTS_REQUIRED);
		}

		int n = CROSSOVER_INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		int index = start;
		while (index < n + start) { // To be perfectly honest, this loop isn't necessary.
			// Select two random groups from 0 to (start-1) to crossover based on their fitness.
			int[] parentIndices = getCrossoverParentsRouletteWheel(state, threadnum);
			Subpopulation[] parents = new Subpopulation[] {
					coopPopulation.getGroups()[parentIndices[0]],
					coopPopulation.getGroups()[parentIndices[1]]
			};

			// Find the common individuals between the two groups.
			Set<Individual> commonInds = new HashSet<Individual>();
			for (int i = 0; i < parents[0].individuals.length; i++) {
				for (int j = 0; j < parents[1].individuals.length; j++) {
					if (parents[0].individuals[i] == parents[1].individuals[j]) {
						commonInds.add(parents[0].individuals[i]);
					}
				}
			}

			// Filter out the common individuals from the two arrays.
			List<Pair<Integer, Individual>> indList1 = new ArrayList<Pair<Integer, Individual>>();
			for (int i = 0; i < parents[0].individuals.length; i++) {
				if (!commonInds.contains(parents[0].individuals[i])) {
					indList1.add(new Pair<Integer, Individual>(i, parents[0].individuals[i]));
				}
			}

			List<Pair<Integer, Individual>> indList2 = getListWithIndices(parents[1].individuals, commonInds);
			for (int i = 0; i < parents[1].individuals.length; i++) {
				if (!commonInds.contains(parents[1].individuals[i])) {
					indList2.add(new Pair<Integer, Individual>(i, parents[1].individuals[i]));
				}
			}

			if (indList1.size() == 0 || indList2.size() == 0) {
				return 0;
			}

			int[] swapIndices = new int[]{
					indList1.get(state.random[threadnum].nextInt(indList1.size())).i1,
					indList2.get(state.random[threadnum].nextInt(indList2.size())).i1,
			};

			int length = CROSSOVER_PARENTS_REQUIRED;

			// Only generate as many children as required.
			for (int c = 0; c < n; c++) {
				// Populate the child group with individuals.
				MLSSubpopulation child = (MLSSubpopulation) parents[c % length].emptyClone();

				int parentLength = parents[c % length].individuals.length;

				for (int ind = 0; ind < parentLength; ind++) {
					if (ind == swapIndices[c]) {
						child.individuals[ind] = parents[(c + 1) % length].individuals[swapIndices[(c + 1) % length]];
					} else {
						child.individuals[ind] = parents[c % length].individuals[ind];
					}
				}

				if (child.individuals.length == 0) {
					state.output.fatal("Error happening in cooperation");
				}

				// Evaluate the child group.
				((MLSEvaluator) state.evaluator).evaluateGroup(state, child, index + c);

				// Add the group to the meta population and the list of entities.
				newPop.subpops[index + c] = child;

				coopPopulation.addGroup(child);
			}

			index += n;
		}

		return n;
	}

	private int[] getCrossoverParentsRouletteWheel(final EvolutionState state, final int threadnum) {
		int length = coopPopulation.getNumGroups();

		double[] copy = new double[length];
		for (int i = 0; i < length; i++) {
			copy[i] = coopPopulation.getGroups()[i].getFitness().fitness();
		}

		int parentIndex1 = rouletteSelect(copy, biasFactor, length, state.random[threadnum]);
		int parentIndex2 = rouletteSelect(copy, biasFactor, length, state.random[threadnum]);

		return new int[]{parentIndex1, parentIndex2};
	}

	private int produceSubpopMutation(final int min,
			final int max,
			final int start,
			final Population newPop,
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
			int parentIndex = getMutationParentRouletteWheel(state, threadnum);
			Subpopulation parent = coopPopulation.getGroups()[parentIndex];

			MLSSubpopulation child = (MLSSubpopulation) parent.emptyClone();

			boolean addIndividual = state.random[threadnum].nextBoolean();
			if (addIndividual || parent.individuals.length == 1) {
				// Copy over the individuals in the parent group.
				child.individuals = new Individual[parent.individuals.length + 1];

				for (int ind = 0; ind < parent.individuals.length; ind++) {
					child.individuals[ind] = parent.individuals[ind];
				}

				Set<Individual> parentInds = new HashSet<Individual>(parent.individuals.length);
				for (Individual ind : parent.individuals) {
					parentInds.add(ind);
				}

				MLSGPIndividual[] allInds = coopPopulation.getIndividuals();

				// Find the individuals in the individual pool that are not already part of the group.
				List<Individual> inds = new ArrayList<Individual>();
				for (int i = 0; i < allInds.length; i++) {
					if (allInds[i] != null && !parentInds.contains(allInds[i])) {
						inds.add(allInds[i]);
					}
				}

				int indIndex = state.random[threadnum].nextInt(inds.size());

				// Add an individual that's not already part of a group to the child group.
				child.individuals[child.individuals.length - 1] = inds.get(indIndex);
			} else {
				// Copy over all but one individual in the parent group.
				child.individuals = new Individual[parent.individuals.length - 1];

				int indIndex = state.random[threadnum].nextInt(parent.individuals.length);

				for (int ind = 0; ind < child.individuals.length; ind++) {
					if (ind < indIndex) {
						child.individuals[ind] = parent.individuals[ind];
					} else {
						child.individuals[ind] = parent.individuals[ind + 1];
					}
				}
			}

			if (child.individuals.length == 0) {
				state.output.fatal("Error happening in mutation");
			}

			// Evaluate the child subpopulation.
			((MLSEvaluator) state.evaluator).evaluateGroup(state, child, index);

			// Add the group to the meta population and the list of entities.
			newPop.subpops[index] = child;

			coopPopulation.addGroup(child);

			index++;
		}

		return n;
	}

	private int getMutationParentRouletteWheel(final EvolutionState state, final int threadnum) {
		int length = coopPopulation.getNumGroups();

		double[] copy = new double[length];
		for (int i = 0; i < length; i++) {
			copy[i] = coopPopulation.getGroups()[i].getFitness().fitness();
		}

		return rouletteSelect(copy, biasFactor, length, state.random[threadnum]);
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
		int numToBreed = state.population.subpops[0].individuals.length;
		int numThreads = Math.min(numToBreed, state.breedthreads);

		if (numThreads < state.breedthreads) {
			state.output.warnOnce("Number of subpopulations to breed (" + numThreads +") is smaller than number of breedthreads (" + state.breedthreads + "), so fewer breedthreads will be created.");
		}

		// Partition the groups into the thread for multithreading purposes.
		int numInds[] = new int[numThreads];
		int from[] = new int[numThreads];

		// We will have some extra individuals.  We distribute these among the early subpopulations.
		int indsPerThread = numToBreed / numThreads;  // integer division
		int slop = numToBreed - numThreads * indsPerThread;

		// Find the number of individuals to generate per thread and where to start the breeding from.
		for (int thread = 0; thread < numThreads; thread++) {
			if (slop > 0) {
				numInds[thread] = indsPerThread + 1;
				slop--;
			} else {
				numInds[thread] = indsPerThread;
			}

			if (numInds[thread] == 0) {
				state.output.warnOnce("More threads exist than can be used to breed some subpopulations");
			}

			// Start from the end of the current population of individuals,
			// and increment the starting points from then on.
			if (thread == 0) {
				from[0] = coopPopulation.getNumIndividuals();
			} else {
				from[thread] = from[thread - 1] + numInds[thread - 1];
			}
		}

		// Breed the groups, i.e., the subpopulations.
		if (numThreads==1) {
			breedIndChunk(newPop, state, numInds[0], from[0], 0);
		} else {
			ThreadPool pool = new ThreadPool();
			for (int i = 0; i < numThreads; i++) {
				IndividualBreederThread r = new IndividualBreederThread();
				r.newPop = newPop;
				r.numInds = numInds[i];
				r.from = from[i];
				r.threadnum = i;
				r.parent = this;
				pool.start(r, "ECJ Breeding Thread " + i);
			}
			pool.joinAll();
		}
	}

	/**
	 * Breed the individuals in the chunk of the subpopulation provided.
	 */
	protected void breedIndChunk(final Population newPop,
			final EvolutionState state,
			final int numInds,
			final int from,
			final int threadnum) {
		BreedingPipeline bp = null;
		if (clonePipelineAndPopulation) {
			bp = (BreedingPipeline) state.population.subpops[0].species.pipe_prototype.clone();
		} else {
			bp = (BreedingPipeline) state.population.subpops[0].species.pipe_prototype;
		}

		// Ensure that the breeding pipeline generates a type of MLSGPIndividual.
		if (!bp.produces(state, newPop, 0, threadnum)) {
			state.output.fatal("The Breeding Pipeline of subpopulation 0 does not produce individuals of the expected species " + newPop.subpops[0].species.getClass().getName() + " or fitness " + newPop.subpops[0].species.f_prototype );
		}

		int index = from;
		int upperBound = from + numInds;
		while (index < upperBound) {
			index += bp.produce(1,
					upperBound-index,
					index,
					0,
					coopPopulation.getIndividuals(),
					state,
					threadnum);
			if (index > upperBound) {
				state.output.fatal("A breeding pipeline overwrote the space of pipeline in subpopulation 0.  You need to check your breeding pipeline code (in produce() ).");
			}
		}

		bp.finishProducing(state, 0, threadnum);
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
		// Load the list of groups into an array to be sorted.
		List<Pair<Subpopulation, Integer>> allGroups = new ArrayList<Pair<Subpopulation, Integer>>(coopPopulation.getNumGroups());
		for (int i = 0; i < coopPopulation.getNumGroups(); i++) {
			allGroups.add(new Pair<Subpopulation, Integer>(coopPopulation.getGroups()[i], i));
		}

		// Load the list of individuals into an array to be sorted.
		List<Pair<Individual, Integer>> allInds = new ArrayList<Pair<Individual, Integer>>(coopPopulation.getNumIndividuals());
		for (int i = 0; i < coopPopulation.getNumIndividuals(); i++) {
			allInds.add(new Pair<Individual, Integer>(coopPopulation.getIndividuals()[i], i));
		}

		Collections.sort(allGroups, groupComparator);
		Collections.sort(allInds, individualComparator);

		// Load the best groups and individuals back into the population.
		for (int i = 1; i < newPop.subpops.length; i++) {
			newPop.subpops[i] = allGroups.get(i - 1).i1;
		}

		for (int i = 0; i < newPop.subpops[0].individuals.length; i++) {
			newPop.subpops[0].individuals[i] = allInds.get(i).i1;
		}
	}

	// Helper method for selecting a subpopulation out of a population based on its fitness.
	protected static final int rouletteSelect(double[] fitnesses, double biasFactor, int length, MersenneTwisterFast rand) {
		// Quick pre-check.
		if (length == 1) {
			return 0;
		}

		double prob = rand.nextDouble();
		double[] buffer = new double[length];

		// Load in the fitnesses into the buffer.
		buffer[0] = Math.pow(fitnesses[0], biasFactor);
		for (int i = 1; i < length; i++) {
			buffer[i] = buffer[i-1] + Math.pow(fitnesses[i], biasFactor);
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

	protected static final int tournamentSelect(double[] fitnesses, int length, double val, MersenneTwisterFast rand) {
		int best = rand.nextInt(length);
		int s = getTournamentSizeToUse(val, rand);

		for (int i = 1; i < s; i++) {
			int j = rand.nextInt(length);

			// Always pick the best.
			if (fitnesses[j] > best) {
				best = j;
			}
		}

		return best;
	}

	private static int getTournamentSizeToUse(double val, MersenneTwisterFast random) {
		if (val == (int) val) {
			return (int) val;
		} else {
			int value = (int) Math.floor(val);
			double p = val - value;
			return (int) val + (random.nextBoolean(p) ? 1 : 0);
		}
    }

	private static <T> List<Pair<Integer, T>> getListWithIndices (T[] array, Set<T> common) {
		return IntStream.range(0, array.length)
				.mapToObj(idx -> new Pair<Integer, T>(idx, array[idx]))
				.filter(pair -> common.contains(pair.i2))
				.collect(Collectors.toList());

	}

	// Threads for breeding subpopulations.
	private class SubpopBreederThread implements Runnable {
		Population newPop;
		int numGroup;
		int from;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {
			parent.breedSubpopChunk(newPop, state, numGroup, from, threadnum);
		}
	}

	// Thread for breeding individuals.
	private class IndividualBreederThread implements Runnable {
		Population newPop;
		int numInds;
		int from;
		EvolutionState state;
		int threadnum;
		MLSBreeder parent;

		public void run() {
			parent.breedIndChunk(newPop, state, numInds, from, threadnum);
		}
	}

	// Compares the fitnesses of the subpopulations.
	private class GroupComparator implements Comparator<Pair<Subpopulation, Integer>> {
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
