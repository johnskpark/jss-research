package app.evolution.multilevel;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.Subpopulation;
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

			bestOfSubpop[s] = subpop.individuals[0];
			for (int i = 1; i < subpop.individuals.length; i++) {
				if (subpop.individuals[i].fitness.betterThan(bestOfSubpop[s].fitness)) {
					bestOfSubpop[s] = subpop.individuals[i];
				}
			}

			if (bestIndsOfRun[s] == null || bestOfSubpop[s].fitness.betterThan(bestIndsOfRun[s].fitness)) {
				bestIndsOfRun[s] = (Individual) (bestOfSubpop[s].clone());
			}
		}

		if (doGeneration) {
			state.output.println("\nGeneration: " + state.generation, statisticsLog);
			state.output.println("Best Individual:", statisticsLog);
		}

		// Print the best fitness of an individual per subpopulation.
		for (int s = 0; s < state.population.subpops.length; s++) {
			if (doGeneration) {
				state.output.println("Subpopulation " + s + ":", statisticsLog);
				bestOfSubpop[s].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				state.output.message("Subpop " + s + " best fitness of generation" + (bestOfSubpop[s].evaluated ? " " : " (evaluated flag not set): ") + bestOfSubpop[s].fitness.fitnessToStringForHumans());
			}

			if (doGeneration && doPerGenerationDescription) {
				// TODO
			}
		}

		// Print the best subpopulation of the generation.
		// TODO

		// Print the best individual of the generation.
		// TODO

	}

	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);

		// TODO
	}

}
