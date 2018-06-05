package app.evolution.multitask.select;

import app.evolution.multitask.MultitaskEvolutionState;
import ec.EvolutionState;
import ec.Individual;
import ec.select.TournamentSelection;

public class MultitaskTournamentSelection extends TournamentSelection {

	private static final long serialVersionUID = 8673832232021030397L;

	private int currentTask;

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int[] tasksForInds = multitaskState.getTasksForInds()[subpopulation];

		int n = INDS_PRODUCED;
		if (n < min) { n = min; }
		if (n > max) { n = max; }

		for(int i = 0; i < n; i++) {
			// Determine the task that the individual will belong to.
			currentTask = tasksForInds[start + i];

			// Start the tournament selection.
			inds[start + i] = state.population.subpops[subpopulation].individuals[produce(subpopulation,state,thread)];
		}
		return n;
	}

	protected int getCurrentTask() {
		return currentTask;
	}

}
