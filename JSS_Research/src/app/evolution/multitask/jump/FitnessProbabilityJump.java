package app.evolution.multitask.jump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.evolution.multitask.IMultitaskNeighbourJump;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class FitnessProbabilityJump implements IMultitaskNeighbourJump {

	private static final long serialVersionUID = 866072103875458209L;

	public static final String P_SEED = "seed";

	private JasimaMultitaskIndividual[][] bestIndsPerTask;
	private JasimaMultitaskIndividual[][] worstIndsPerTask;

//	private List<JasimaMultitaskIndividual>[][] individualsPerTask;
	private int numSubpops;
	private int numTasks;
	private MersenneTwisterFast rand;
	private int initSeed;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// Use the default seed.
		initSeed = state.parameters.getInt((new Parameter(P_SEED)).push(""+0), null);

		rand = new MersenneTwisterFast(initSeed);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preprocessing(final EvolutionState state, final int threadnum) {
		if (state.generation == 0) {
			MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

			numSubpops = multitaskState.population.subpops.length;
			numTasks = multitaskState.getNumTasks();

			bestIndsPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
			worstIndsPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
//			individualsPerTask = new List[numSubpops][numTasks];
//			for (int s = 0; s < numSubpops; s++) {
//				for (int t = 0; t < numTasks; t++) {
//					individualsPerTask[s][t] = new ArrayList<>();
//				}
//			}
		} else {
			for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
				Individual[] inds = state.population.subpops[subpop].individuals;
				for (int i = 0; i < inds.length; i++) {
					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[i];

					for (int task = 0; task < numTasks; task++) {
						if (bestIndsPerTask[subpop][i] == null || ind.taskFitnessBetterThan(bestIndsPerTask[subpop][i], task)) {
							bestIndsPerTask[subpop][i] = ind;
						}

						if (worstIndsPerTask[subpop][i] == null || worstIndsPerTask[subpop][i].taskFitnessBetterThan(ind, task)) {
							worstIndsPerTask[subpop][i] = ind;
						}
					}
				}
			}

//			// Insert the individuals into the respective slots.
//			for (int i = 0; i < state.population.subpops.length; i++) {
//				Individual[] inds = state.population.subpops[i].individuals;
//				for (int j = 0; j < inds.length; j++) {
//					JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[j];
//
//					for (int task = 0; task < numTasks; task++) {
//						if (ind.getTaskFitness(task) != MultitaskKozaFitness.NOT_SET) {
//							individualsPerTask[i][task].add(ind);
//						}
//					}
//				}
//			}
//
//			// Sort the individuals into their respective ranks.
//			for (int i = 0; i < numSubpops; i++) {
//				for (int j = 0; j < numTasks; j++) {
//					Collections.sort(individualsPerTask[i][j], new FitnessComparator(j));
//				}
//			}
		}
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
//		List<JasimaMultitaskIndividual> indList = individualsPerTask[subpopulation][currentTask];
//
//		int rank = indList.indexOf(ind);
//		double prob = 1.0 * (indList.size() - rank) / indList.size();
//
//		return rand.nextBoolean(prob);

		// TODO
		return false;
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
//		FitnessComparator comparator = new FitnessComparator(task);
//		List<JasimaMultitaskIndividual> indList = individualsPerTask[subpopulation][task];
//
//		// Just do binary sort
//		int low = 0;
//		int high = indList.size() - 1;
//
//		// FIXME this part needs to be tested.
//		while (low < high) {
//			int mid = (low + high) / 2;
//
//			JasimaMultitaskIndividual other = indList.get(mid);
//			int compare = comparator.compare(ind, other);
//			if (compare > 0) {
//				// Mid point has better fitness, move the individual higher in the list.
//				low = mid + 1;
//			} else if (compare < 0) {
//				// Mid point has worse, move the individual lower in the list.
//				high = mid - 1;
//			} else {
//				low = high = mid;
//			}
//		}
//
//		// Ensure we're not adding in a duplicate here.
//		int start = low;
//		boolean isDuplicate = false;
//		while (comparator.compare(ind, indList.get(start)) == 0 && !isDuplicate && start < indList.size()) {
//			if (ind.equals(indList.get(start))) {
//				isDuplicate = true;
//			}
//			start++;
//		}
//
//		if (!isDuplicate) {
//			indList.add(low, ind);
//		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				bestIndsPerTask[i][j] = null;
				worstIndsPerTask[i][j] = null;
			}
		}
	}

	private class FitnessComparator implements Comparator<JasimaMultitaskIndividual> {
		private int task;

		public FitnessComparator(int task) {
			this.task = task;
		}

		@Override
		public int compare(JasimaMultitaskIndividual o1, JasimaMultitaskIndividual o2) {
			if (o1.taskFitnessBetterThan(o2, task)) {
				return -1;
			} else if (o2.taskFitnessBetterThan(o1, task)) {
				return 1;
			} else {
				return 0;
			}

//			double tf1 = o1.getTaskFitness(task);
//			double tf2 = o2.getTaskFitness(task);
//
//			// Compare the task fitnesses.
//			if (tf1 < tf2) {
//				return -1;
//			} else if (tf1 > tf2) {
//				return 1;
//			} else {
//				// Compare their general fitnesses
//				if (o1.getFitness().betterThan(o2.getFitness())) {
//					return -1;
//				} else if (o2.getFitness().betterThan(o1.getFitness())) {
//					return 1;
//				} else {
//					return 0;
//				}
//			}
		}

	}

}
