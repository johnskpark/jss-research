package app.evolution.multitask.initTask;

import java.util.Arrays;

import app.evolution.multitask.IMultitaskInitTaskStrategy;
import app.evolution.multitask.JasimaMultitaskIndividual;
import app.evolution.multitask.MultitaskEvolutionState;
import ec.Subpopulation;

public class RoundRobinTaskStrategy implements IMultitaskInitTaskStrategy {

	@Override
	public void initTasksForInds(MultitaskEvolutionState state) {
		int numTasks = state.getNumTasks();

		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			Arrays.stream(subpop.individuals).forEach(x -> ((JasimaMultitaskIndividual) x).setNumTasks(numTasks));

			int numIndsPerTask = subpop.individuals.length / numTasks;
			int remainder = subpop.individuals.length - (numIndsPerTask * numTasks);

			int taskCount = 0;
			int taskIndex = 0;
			for (int j = 0; j < subpop.individuals.length; j++) {
				((JasimaMultitaskIndividual) subpop.individuals[j]).setAssignedTask(taskIndex);

				taskCount++;
				if (taskCount >= numIndsPerTask + ((taskIndex < remainder) ? 1 : 0)) {
					taskCount = 0;
					taskIndex++;
				}
			}
		}
	}

}
