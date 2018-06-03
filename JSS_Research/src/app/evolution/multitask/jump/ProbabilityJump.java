package app.evolution.multitask.jump;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.evolution.multitask.IMultitaskNeighbourJump;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;

public class ProbabilityJump implements IMultitaskNeighbourJump {

	private List<JasimaMultitaskIndividual>[][] individualsPerTask;
	private int numSubpops;
	private int numTasks;
	private MersenneTwisterFast rand;

	@SuppressWarnings("unchecked")
	@Override
	public void init(final EvolutionState state) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		numSubpops = multitaskState.population.subpops.length;
		numTasks = multitaskState.getNumTasks();

		individualsPerTask = new List[numSubpops][numTasks];
		for (int s = 0; s < numSubpops; s++) {
			for (int t = 0; t < numTasks; t++) {
				individualsPerTask[s][t] = new ArrayList<>();
			}
		}

		rand = new MersenneTwisterFast(state.random[0].nextLong());
	}

	@Override
	public void preprocessing(final EvolutionState state, final int threadnum) {
		// Insert the individuals into the respective slots.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Individual[] inds = state.population.subpops[i].individuals;
			for (int j = 0; j < inds.length; j++) {
				JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[j];
				individualsPerTask[i][ind.getAssignedTask()].add(ind);
			}
		}

		// Sort the individuals into their respective ranks.
		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				Collections.sort(individualsPerTask[i][j], new FitnessComparator(j));
			}
		}
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		List<JasimaMultitaskIndividual> indList = individualsPerTask[subpopulation][currentTask];

		int rank = indList.indexOf(ind);
		double prob = 1.0 * (indList.size() - rank) / indList.size();

		return rand.nextBoolean(prob);
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		FitnessComparator comparator = new FitnessComparator(task);
		List<JasimaMultitaskIndividual> indList = individualsPerTask[subpopulation][task];

		// Just do binary sort
		int low = 0;
		int high = indList.size() - 1;

		// FIXME this part needs to be tested.
		while (low < high) {
			int mid = (low + high) / 2;

			JasimaMultitaskIndividual other = indList.get(mid);
			int compare = comparator.compare(ind, other);
			if (compare > 0) {
				// Mid point has better fitness, move the individual higher in the list.
				low = mid + 1;
			} else if (compare < 0) {
				// Mid point has worse, move the individual lower in the list.
				high = mid - 1;
			} else {
				low = high = mid;
			}
		}

		indList.add(low, ind);
	}

	@Override
	public void clear() {
		for (int i = 0; i < numSubpops; i++) {
			for (int j = 0; j < numTasks; j++) {
				individualsPerTask[i][j].clear();
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
			if (o1.getTaskFitness(task) < o2.getTaskFitness(task)) {
				return -1;
			} else if (o2.getTaskFitness(task) > o2.getTaskFitness(task)) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
