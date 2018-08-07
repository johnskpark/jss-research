package app.evolution.multitask;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;

public class JasimaMultitaskStatistics extends SimpleStatistics {

	private static final long serialVersionUID = 9036462995622832702L;

	private JasimaMultitaskIndividual[][] bestIndPerTask = null;
	private JasimaMultitaskIndividual[] bestIndOfGen = null;

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		bestIndPerTask = new JasimaMultitaskIndividual[numSubpops][numTasks];
		bestIndOfGen = new JasimaMultitaskIndividual[numSubpops];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		// Collect the standarded fitnesses of the individuals.
		for (int i = 0; i < numSubpops; i++) {
			Individual[] inds = state.population.subpops[i].individuals;

			// Carry out fitness comparisons between the current individual and the best individual found so far.
			for (int j = 0; j < inds.length; j++) {
				JasimaMultitaskIndividual ind = (JasimaMultitaskIndividual) inds[j];

				if (ind.evaluated) {
					// Jasima problems use KozaFitness.
					if (bestIndOfGen[i] == null || ind.fitness.betterThan(bestIndOfGen[i].fitness)) {
						bestIndOfGen[i] = ind;
					}

					// For multitask fitness.
					for (int k = 0; k < numTasks; k++) {
						if (ind.getTaskFitness(k) == MultitaskKozaFitness.NOT_SET) {
							continue;
						}

						if (bestIndPerTask[i][k] == null || ind.taskFitnessBetterThan(bestIndPerTask[i][k], k)) {
							bestIndPerTask[i][k] = ind;
						}
					}
				}
			}

			// Print out a summary of the individual's fitnesses.
			if (doGeneration) { state.output.println("\n Best Multitask Individuals of Subpopulation " + i + ":",statisticslog); }
			for (int j = 0; j < numTasks; j++) {
				if (doGeneration) {
					state.output.println("Task: " + j, statisticslog);
					bestIndPerTask[i][j].printIndividualForHumans(state, statisticslog);
				}
				if (doMessage && !silentPrint) {
					state.output.message("Subpop " + i + " task " + j + " best fitness of generation" +
							(bestIndPerTask[i][j].evaluated ? " " : " (evaluated flag not set): ") +
							bestIndPerTask[i][j].fitness.fitnessToStringForHumans());
				}
			}
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);

		MultitaskEvolutionState multitaskState = (MultitaskEvolutionState) state;

		int numSubpops = multitaskState.population.subpops.length;
		int numTasks = multitaskState.getNumTasks();

		// For now we just print the best fitness of last generation of subpopulation i.

		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for (int i = 0; i < numSubpops; i++) {
			// Print out the best overall individual.
			if (doFinal) {
				state.output.println("Subpopulation " + i + ":", statisticslog);
				bestIndOfGen[i].printIndividualForHumans(state, statisticslog);
			}

			if (doMessage && !silentPrint) { state.output.message("Subpop " + i + " best fitness of run: " + bestIndOfGen[i].fitness.fitnessToStringForHumans()); }

			// finally describe the winner if there is a description
			if (doFinal && doDescription) {
				if (state.evaluator.p_problem instanceof SimpleProblemForm) {
					((SimpleProblemForm) (state.evaluator.p_problem.clone())).describe(state, bestIndOfGen[i], 0, 0, statisticslog);
				}
			}

			// Print out the best individuals per task.
			if (doFinal) { state.output.println("\nBest Multitask Individual of Subpopulation " + i + ":", statisticslog); }
			for (int j = 0; j < numTasks; j++) {
				JasimaMultitaskIndividual taskInd = bestIndPerTask[i][j];
				if (doFinal) {
					state.output.println("Task " + j + ":", statisticslog);
					taskInd.printIndividualForHumans(state, statisticslog);
				}

				if (doMessage && !silentPrint) { state.output.message("Subpop " + i + " task " + j + " best fitness of run: " + taskInd.fitness.fitnessToStringForHumans()); }
			}
		}
	}


}
