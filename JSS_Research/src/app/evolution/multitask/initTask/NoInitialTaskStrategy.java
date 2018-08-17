package app.evolution.multitask.initTask;

import java.util.Arrays;

import app.evolution.multitask.IMultitaskInitTaskStrategy;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import ec.Subpopulation;

public class NoInitialTaskStrategy implements IMultitaskInitTaskStrategy {

	@Override
	public void initTasksForInds(MultitaskEvolutionState state) {
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			Arrays.stream(subpop.individuals).forEach(x -> ((JasimaMultitaskIndividual) x).setAssignedTask(JasimaMultitaskIndividual.NO_TASK_SET));
		}
	}

}
