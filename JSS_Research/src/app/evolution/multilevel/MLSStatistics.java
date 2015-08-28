package app.evolution.multilevel;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.util.Output;
import ec.util.Parameter;

public class MLSStatistics extends Statistics {

	public static final String P_STATISTICS_FILE = "file";

	public static final String P_COMPRESS = "gzip";

	public static final String P_DO_FINAL = "do-final";
	public static final String P_DO_GENERATION = "do-generation";
	public static final String P_DO_MESSAGE = "do-message";
	public static final String P_DO_DESCRIPTION = "do-description";
	public static final String P_DO_PER_GENERATION_DESCRIPTION = "do-per-generation-description";

	private int statisticsLog = 0; // stdout

	private Individual[] bestIndsOfRun = null;
	private MLSSubpopulation bestSubpopOfRun = null;

	private boolean compress;
	private boolean doFinal;
	private boolean doGeneration;
	private boolean doMessage;
	private boolean doDescription;
	private boolean doPerGenerationDescription;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		compress = state.parameters.getBoolean(base.push(P_COMPRESS), null, false);

		File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);

		if (silentFile) {
			statisticsLog = Output.NO_LOGS;
		} else if (statisticsFile != null) {
			try {
				statisticsLog = state.output.addLog(statisticsFile, !compress, compress);
			} catch (IOException ex) {
				state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + ex);
			}
		} else {
			state.output.warning("No statistics file specified, printing to stdout at end.", base.push(P_STATISTICS_FILE));
		}
	}

	public void postInitialisationStatsitics(final EvolutionState state) {
		super.postInitializationStatistics(state);

		// Set up the bestOfRun array. This cannot be carried out in setup,
		// as the number of subpopulation may not have been determined yet.
		bestIndsOfRun = new Individual[state.population.subpops.length];
	}

	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		Individual[] bestOfSubpop = new Individual[state.population.subpops.length];
		MLSSubpopulation bestSubpop = null;

		for (int s = 0; s < state.population.subpops.length; s++) {
			MLSSubpopulation subpop = (MLSSubpopulation) state.population.subpops[s];

			// Find the best individuals from each subpopulation of the generation.
			bestOfSubpop[s] = subpop.individuals[0];
			for (int i = 1; i < subpop.individuals.length; i++) {
				if (subpop.individuals[i].fitness.betterThan(bestOfSubpop[s].fitness)) {
					bestOfSubpop[s] = subpop.individuals[i];
				}
			}

			// Find the best subpopulation of the generation.
			if (bestSubpop == null || subpop.getFitness().betterThan(bestSubpop.getFitness())) {
				bestSubpop = subpop;
			}

			// Update the best individuals of the run.
			if (bestIndsOfRun[s] == null || bestOfSubpop[s].fitness.betterThan(bestIndsOfRun[s].fitness)) {
				bestIndsOfRun[s] = (Individual) (bestOfSubpop[s].clone());
			}
		}

		// Update the best subpopulation of the run.
		if (bestSubpopOfRun == null || bestSubpop.getFitness().betterThan(bestSubpopOfRun.getFitness())) {
			bestSubpopOfRun = bestSubpop;
		}


		// Print the current generation.
		if (doGeneration) {
			state.output.println("\nGeneration: " + state.generation, statisticsLog);
			state.output.println("Best Individual:", statisticsLog);
		}

		// Print the best fitness of an individual per subpopulation.
		for (int s = 0; s < state.population.subpops.length; s++) {
			MLSSubpopulation subpop = (MLSSubpopulation) state.population.subpops[s];

			if (doGeneration) {
				state.output.println("Subpopulation " + s + ": " + subpop.getFitness().fitnessToStringForHumans(), statisticsLog);
				bestOfSubpop[s].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				state.output.message("Subpop " + s + " best fitness of generation" + (bestOfSubpop[s].evaluated ? " " : " (evaluated flag not set): ") + bestOfSubpop[s].fitness.fitnessToStringForHumans());
			}
		}
	}

	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);

		// I need to print out the best individuals and the best subpopulation.

		// TODO Right, what do I need to write here?
		// Print out the best individuals of each subpopulation.
		if (doFinal) {
			// TODO
		}
		for (int s = 0; s < state.population.subpops.length; s++) {
			if (doFinal) {
				// TODO
			}

			if (doMessage && !silentPrint) {
				// TODO
			}
		}

		// Print out the individuals making up the best subpopulation.
		if (doFinal) {
			// TODO
		}
	}

}
