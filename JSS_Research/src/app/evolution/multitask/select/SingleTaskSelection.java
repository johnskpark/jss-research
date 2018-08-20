package app.evolution.multitask.select;

import java.util.ArrayList;
import java.util.List;

import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import app.evolution.multitask.MultitaskKozaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class SingleTaskSelection extends MultitaskTournamentSelection {

	private static final long serialVersionUID = -2081578565362533642L;

	private List<Integer> indList = new ArrayList<>();
	private int currentTask = MultitaskKozaFitness.NOT_SET;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;
		int currentTask = getCurrentTask();

		setCurrentTask(multitaskState, subpopulation, currentTask);

		int size = getTournamentSizeToUse(state.random[thread]);
		int[] tournamentInds = new int[size];

		// Pick the number of individuals randomly from the list, and then fill in the missing rank values.
		for (int i = 0; i < size; i++) {
			int index = state.random[thread].nextInt(indList.size());
			tournamentInds[i] = indList.get(index);
		}

		// Calculate the scores, with the highest score being the best.
		double[] scores = calculateScores(state, subpopulation, currentTask, tournamentInds, thread);

		int bestIndex = 0;
		for (int i = 1; i < size; i++) {
			if (pickWorst) {
				if (scores[bestIndex] > scores[i]) {
					bestIndex = i;
				}
			} else {
				if (scores[bestIndex] < scores[i]) {
					bestIndex = i;
				}
			}
		}

		return tournamentInds[bestIndex];
	}

	protected void setCurrentTask(final MultitaskEvolutionState state,
			final int subpopulation,
			final int task) {
		if (currentTask != task) {
			getViableInds(state, subpopulation, task);
			currentTask = task;
		}
	}

	protected void getViableInds(final MultitaskEvolutionState state,
			final int subpopulation,
			final int task) {
		if (!indList.isEmpty()) {
			indList.clear();
		}

		Individual[] inds = state.population.subpops[subpopulation].individuals;
		for (int i = 0; i < inds.length; i++) {
			JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[i];

			if (ind.getTaskFitness(task) != MultitaskKozaFitness.NOT_SET) {
				indList.add(i);
			}
		}
	}

	protected double[] calculateScores(final EvolutionState state,
			final int subpopulation,
			final int currentTask,
			final int[] tournamentInds,
			final int thread) {
		double[] scores = new double[tournamentInds.length];

		Individual[] inds = state.population.subpops[subpopulation].individuals;
		for (int i = 0; i < scores.length; i++) {
			JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[tournamentInds[i]];

			scores[i] = ind.getTaskFitness(currentTask);
		}

		return scores;
	}

}
