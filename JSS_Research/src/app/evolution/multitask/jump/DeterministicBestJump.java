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

public class DeterministicBestJump implements IMultitaskNeighbourJump {

	private List<JasimaMultitaskIndividual>[][] individualsPerTask;
	private int numSubpops;
	private int numTasks;

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
				Collections.sort(individualsPerTask[i][j], new FitnessComparator());
			}
		}
	}

	@Override
	public boolean jumpToNeighbour(final EvolutionState state,
			final int subpop,
			final int currentTask,
			final int neighbourTask,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// TODO
		return false;
	}

	@Override
	public void addIndividualToTask(final EvolutionState state,
			final int subpopulation,
			final int task,
			final JasimaMultitaskIndividual ind,
			final int threadnum) {
		// TODO
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
		@Override
		public int compare(JasimaMultitaskIndividual o1, JasimaMultitaskIndividual o2) {
			if (o1.getFitness().betterThan(o2.getFitness())) {
				return -1;
			} else if (o2.getFitness().betterThan(o1.getFitness())) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}
