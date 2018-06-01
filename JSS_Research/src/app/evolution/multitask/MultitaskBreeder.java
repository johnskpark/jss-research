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
import jasima.core.util.Pair;

public class MultitaskBreeder extends SimpleBreeder {

	private static final long serialVersionUID = -1963270030018084823L;

	public static final String P_BREEDING_STRATEGY = "breed-strategy";

	private IMultitaskBreedStrategy breedStrategy;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		breedStrategy = (IMultitaskBreedStrategy) state.parameters.getInstanceForParameter(base.push(P_BREEDING_STRATEGY), null, IMultitaskBreedStrategy.class);
	}

	@Override
	public Population breedPopulation(EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		multitaskState.setNumIndsPerTask(state, breedStrategy.getNumIndsPerTask(multitaskState));
		addIndsToTasks(state);

		Population newPop = super.breedPopulation(state);

		clearIndTaskLists(state);

		return newPop;
	}

	@Override
	// TODO this is a copy of the old code, I need to assign the task to the individuals here.
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

				x = from[subpop];
				int upperbound = from[subpop] + numinds[subpop];
				while (x < upperbound) {
					x += bp.produce(1, upperbound - x, x, subpop,
							newpop.subpops[subpop].individuals,
							state,threadnum);
				}
				if (x > upperbound) { // uh oh!  Someone blew it!
					state.output.fatal("Whoa! A breeding pipeline overwrote the space of another pipeline in subpopulation " + subpop + ".  You need to check your breeding pipeline code (in produce() ).");
				}

				bp.finishProducing(state, subpop, threadnum);
			}
		}
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

						if (!indList.contains(multitaskInd)) {
							indList.add(multitaskInd);
							indIndicesList.add(i);
						} else {
							state.output.fatal("Attempting to add duplicate individual to task " + multitaskInd);
						}
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

				for (Pair<Integer, JasimaMultitaskIndividual> pair : indIndexPair[i][j]) {
					multitaskState.getIndsPerTask()[i][j].add(pair.a);
				}
			}
		}
	}

	protected void clearIndTaskLists(EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		List<Integer>[][] indsPerTask = multitaskState.getIndsPerTask();
		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				indsPerTask[i][j].clear();
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
			double tf1 = o1.b.getTaskFitness(task);
			double tf2 = o2.b.getTaskFitness(task);

			if (tf1 < tf2) {
				return -1;
			} else if (tf1 > tf2) {
				return 1;
			} else {
				return 0;
			}
		}

	}
}
