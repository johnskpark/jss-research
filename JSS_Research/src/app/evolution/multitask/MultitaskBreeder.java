package app.evolution.multitask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparatorL;
import jasima.core.util.Pair;

public class MultitaskBreeder extends SimpleBreeder {

	private static final long serialVersionUID = -1963270030018084823L;

	public static final String P_BREEDING_STRATEGY = "strategy";

	private IMultitaskBreedStrategy breedStrategy;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		breedStrategy = (IMultitaskBreedStrategy) state.parameters.getInstanceForParameter(base.push(P_BREEDING_STRATEGY), null, IMultitaskBreedStrategy.class);
	}

	@Override
	public int numElites(EvolutionState state, int subpopulation) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numElites = super.numElites(state, subpopulation);

		return numElites * (multitaskState.getNumTasks());
	}

	@Override
	public Population breedPopulation(EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		multitaskState.setTasksForInds(breedStrategy.getTasksForInds(multitaskState, this));
		addIndsToTasks(state);

		Population newPop = super.breedPopulation(state);

		clearIndTaskLists(state);

		return newPop;
	}

	@Override
	protected void breedPopChunk(Population newpop,
			EvolutionState state,
			int[] numinds,
			int[] from,
			int threadnum) {
		for(int subpop = 0; subpop < newpop.subpops.length; subpop++) {
			// if it's subpop's turn and we're doing sequential breeding...
			if (!shouldBreedSubpop(state, subpop, threadnum))  {
				// instead of breeding, we should just copy forward this subpopulation.  We'll copy the part we're assigned
				for (int ind = from[subpop]; ind < numinds[subpop] - from[subpop]; ind++) {
					// newpop.subpops[subpop].individuals[ind] = (Individual)(state.population.subpops[subpop].individuals[ind].clone());
					// this could get dangerous
					newpop.subpops[subpop].individuals[ind] = state.population.subpops[subpop].individuals[ind];
				}
			} else {
				// do regular breeding of this subpopulation
				BreedingPipeline bp = null;
				if (clonePipelineAndPopulation) {
					bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype.clone();
				} else {
					bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype;
				}

				// check to make sure that the breeding pipeline produces
				// the right kind of individuals.  Don't want a mistake there! :-)
				int x;
				if (!bp.produces(state, newpop, subpop, threadnum)) {
					state.output.fatal("The Breeding Pipeline of subpopulation " + subpop + " does not produce individuals of the expected species " + newpop.subpops[subpop].species.getClass().getName() + " or fitness " + newpop.subpops[subpop].species.f_prototype );
				}
				bp.prepareToProduce(state, subpop, threadnum);

				// start breedin'!
				MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

				int[] tasksForInds = multitaskState.getTasksForInds()[subpop];

				x = from[subpop];
				int upperbound = from[subpop] + numinds[subpop];
				while (x < upperbound) {
					int indsGenerated = bp.produce(1, upperbound - x, x, subpop,
							newpop.subpops[subpop].individuals,
							state,threadnum);

					for (int i = 0 ; i < indsGenerated; i++) {
						JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) newpop.subpops[subpop].individuals[x + i];
						ind.setAssignedTask(tasksForInds[x + i]);
					}

					x += indsGenerated;
				}
				if (x > upperbound) { // uh oh!  Someone blew it!
					state.output.fatal("Whoa! A breeding pipeline overwrote the space of another pipeline in subpopulation " + subpop + ".  You need to check your breeding pipeline code (in produce() ).");
				}

				bp.finishProducing(state, subpop, threadnum);
			}
		}
	}

	@Override
	protected void loadElites(EvolutionState state, Population newpop) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		for(int x = 0; x < numSubpops; x++)  {
			// Are our elites small enough?
			if (numElites(state, x) > state.population.subpops[x].individuals.length) {
				state.output.error("The number of elites for subpopulation " + x + " exceeds the actual size of the subpopulation", new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push("" + x));
			}
			if (numElites(state, x) == state.population.subpops[x].individuals.length) {
				state.output.warning("The number of elites for subpopulation " + x + " is the actual size of the subpopulation", new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push("" + x));
			}

			// The number of elites need to be divisible by the number of tasks + 1
			if (numElites(state, x) % numTasks != 0) {
				state.output.warning("The number of elites for subpopulation " + x + " needs to be divisible by the number of tasks. Number of elites: " + numElites(state, x), new Parameter(EvolutionState.P_BREEDER).push(P_ELITE).push("" + x));
			}
		}
		state.output.exitIfErrors();

		// We assume that we're only grabbing a small number (say <10%), so it's not being done multithreaded
		for (int sub = 0; sub < numSubpops; sub++)  {
			if (!shouldBreedSubpop(state, sub, 0)) { // Don't load the elites for this one, we're not doing breeding of it
				continue;
			}

			Individual[] oldinds = state.population.subpops[sub].individuals;
			int numElites = numElites(state, sub);

			// If the number of elites equals the number of tasks, then we handle this by just finding the best elites for each task.
			if (numElites == numTasks) {
				int[] best = new int[numElites];
				for(int x = 1; x < oldinds.length; x++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) oldinds[x];

					// Best fitness for specific tasks.
					for (int task = 0; task < numElites; task++) {
						JasimaMultitaskIndividual bestInd = (JasimaMultitaskIndividual) oldinds[best[task]];

						// So why did I do this again?
						if (bestInd.getTaskFitness(task) == MultitaskKozaFitness.NOT_SET ||
								ind.taskFitnessBetterThan(bestInd, task)) {
							best[task] = x;
						}
					}
				}
				Individual[] inds = newpop.subpops[sub].individuals;
				for (int task = 0; task < numElites; task++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) oldinds[best[task]];
					ind.setAssignedTask(task);

					inds[inds.length-task-1] = (Individual) (ind.clone());
				}
			}  else if (numElites > numTasks) { // We'll need to sort for each task.
				int increment = 0;

				// Load individuals per task.
				for (int task = 0; task < numTasks; task++) {
					increment += loadElitesPerTask(state, newpop, sub, task, numTasks, increment);
				}
			}
		}

		// Optionally force reevaluation.
		unmarkElitesEvaluated(state, newpop);
	}

	protected int loadElitesPerTask(EvolutionState state,
			Population newpop,
			int subpopulation,
			int task,
			int numTasks,
			int increment) {
		// This is bugged, it doesn't assign the (Assign the what???)
		Individual[] oldinds = state.population.subpops[subpopulation].individuals;
		int numLoaded = numElites(state, subpopulation) / numTasks;

		int[] orderedPop = new int[oldinds.length];
		for (int x = 0; x < oldinds.length; x++) { orderedPop[x] = x; }

		// Sort the best so far where "<" means "not as fit as".
		QuickSort.qsort(orderedPop, new EliteTaskComparator(oldinds, task));
		// Load the top N individuals.

		Individual[] inds = newpop.subpops[subpopulation].individuals;
		for(int x = inds.length - (increment + numLoaded); x < inds.length - increment; x++) {
			JasimaMultitaskIndividual newind = (JasimaMultitaskIndividual) (oldinds[orderedPop[x]].clone());
			newind.setAssignedTask(task);

			inds[x] = newind;
		}

		return numLoaded;
	}

	@SuppressWarnings("unchecked")
	protected void addIndsToTasks(EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		List<JasimaMultitaskIndividual>[][] indsPerTask = new List[numSubpops][numTasks];
		List<Integer>[][] indIndicesPerTask = new List[numSubpops][numTasks];

		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				indsPerTask[i][j] = new ArrayList<>();
				indIndicesPerTask[i][j] = new ArrayList<>();
			}
		}

		// Add the individuals to the list.
		for (int s = 0; s < numSubpops; s++) {
			Individual[] inds = multitaskState.population.subpops[s].individuals;

			for (int i = 0; i < inds.length; i++) {
				JasimaMultitaskIndividual multitaskInd = (JasimaMultitaskIndividual) inds[i];

				for (int t = 0; t < multitaskState.getNumTasks(); t++) {
					if (multitaskInd.getTaskFitness(t) != NOT_SET) {
						List<JasimaMultitaskIndividual> indList = indsPerTask[s][t];
						List<Integer> indIndicesList = indIndicesPerTask[s][t];

						indList.add(multitaskInd);
						indIndicesList.add(i);
					}
				}
			}
		}

		// Sort the individuals by their fitnesses and add them to the state.
		List<Pair<Integer, JasimaMultitaskIndividual>>[][] indIndexPair = new List[numSubpops][numTasks];

		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				List<JasimaMultitaskIndividual> indList = indsPerTask[i][j];
				List<Integer> indIndicesList = indIndicesPerTask[i][j];

				indIndexPair[i][j] = new ArrayList<>();

				for (int k = 0; k < indList.size(); k++) {
					indIndexPair[i][j].add(new Pair<Integer, JasimaMultitaskIndividual>(indIndicesList.get(k), indList.get(k)));
				}

				Collections.sort(indIndexPair[i][j], new TaskFitnessComparator(j));

				double lastFitness = 0.0;
				int rank = 0;
				for (Pair<Integer, JasimaMultitaskIndividual> pair : indIndexPair[i][j]) {
					multitaskState.getIndsPerTask()[i][j].add(pair.a);

					double fitness = pair.b.getTaskFitness(j);
					if (fitness != lastFitness) {
						lastFitness = fitness;
						rank++;
					}
					multitaskState.getRanksPerTask()[i][j].add(rank);
				}
			}
		}
	}

	protected void clearIndTaskLists(EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				multitaskState.getIndsPerTask()[i][j].clear();
				multitaskState.getRanksPerTask()[i][j].clear();
			}
		}
	}

	private class TaskFitnessComparator implements Comparator<Pair<Integer, JasimaMultitaskIndividual>> {
		private int task;

		public TaskFitnessComparator(int task) {
			this.task = task;
		}

		@Override
		public int compare(Pair<Integer, JasimaMultitaskIndividual> o1, Pair<Integer, JasimaMultitaskIndividual> o2) {
			if (o1.b.taskFitnessBetterThan(o2.b, task)) {
				return -1;
			} else if (o2.b.taskFitnessBetterThan(o1.b, task)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private class EliteTaskComparator implements SortComparatorL {
		Individual[] inds;
		int task;

		public EliteTaskComparator(Individual[] inds, int task) {
			this.inds = inds;
			this.task = task;
		}

		@Override
		public boolean lt(long a, long b) {
			if (task == JasimaMultitaskIndividual.NO_TASK_SET) {
				return inds[(int)b].fitness.betterThan(inds[(int)a].fitness);
			} else {
				JasimaMultitaskIndividual ind1 = (JasimaMultitaskIndividual) inds[(int)a];
				JasimaMultitaskIndividual ind2 = (JasimaMultitaskIndividual) inds[(int)b];
				return ind2.taskFitnessBetterThan(ind1, task);
			}
		}

		@Override
		public boolean gt(long a, long b) {
			if (task == JasimaMultitaskIndividual.NO_TASK_SET) {
				return inds[(int)a].fitness.betterThan(inds[(int)b].fitness);
			} else {
				JasimaMultitaskIndividual ind1 = (JasimaMultitaskIndividual) inds[(int)a];
				JasimaMultitaskIndividual ind2 = (JasimaMultitaskIndividual) inds[(int)b];
				return ind1.taskFitnessBetterThan(ind2, task);
			}
		}
	}
}
