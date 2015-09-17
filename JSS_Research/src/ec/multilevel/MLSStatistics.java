package ec.multilevel;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.util.Output;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MLSStatistics extends Statistics {

	private static final long serialVersionUID = 2693112218608601218L;

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
	private int bestSubpopIndexOfRun = -1;

	private boolean compress;
	private boolean doFinal;
	private boolean doGeneration;
	private boolean doMessage;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		compress = state.parameters.getBoolean(base.push(P_COMPRESS), null, false);

		File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);

        doFinal = state.parameters.getBoolean(base.push(P_DO_FINAL),null,true);
        doGeneration = state.parameters.getBoolean(base.push(P_DO_GENERATION),null,true);
        doMessage = state.parameters.getBoolean(base.push(P_DO_MESSAGE),null,true);

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

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		// Set up the bestOfRun array. This cannot be carried out in setup,
		// as the number of subpopulation may not have been determined yet.
		bestIndsOfRun = new Individual[state.population.subpops.length];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		Individual[] bestOfSubpop = new Individual[state.population.subpops.length];
		MLSSubpopulation bestSubpop = null;
		int bestSubpopIndex = -1;

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
				bestSubpopIndex = s;
			}

			// Update the best individuals of the run.
			if (bestIndsOfRun[s] == null || bestOfSubpop[s].fitness.betterThan(bestIndsOfRun[s].fitness)) {
				bestIndsOfRun[s] = (Individual) (bestOfSubpop[s].clone());
			}
		}

		// Update the best subpopulation of the run.
		if (bestSubpopOfRun == null || bestSubpop.getFitness().betterThan(bestSubpopOfRun.getFitness())) {
			bestSubpopOfRun = (MLSSubpopulation) bestSubpop.emptyClone();
			for (int i = 0; i < bestSubpop.individuals.length; i++) {
				bestSubpopOfRun.individuals[i] = (Individual) bestSubpop.individuals[i].clone();
			}

			bestSubpopIndexOfRun = bestSubpopIndex;
		}

		System.out.println("Best subpop size: " + bestSubpopOfRun.individuals.length);


		// Print the current generation.
		if (doGeneration) {
			state.output.println("\nGeneration: " + state.generation, statisticsLog);
			state.output.println("Best Individual:", statisticsLog);
		}

		// Print the best fitness of a subpopulation.
		if (doGeneration) {
			String evaluatedStr;

			if (bestSubpop.isEvaluated()) {
				evaluatedStr = "";
			} else {
				evaluatedStr = "(evaluated flag not set): ";
			}

			state.output.message("Best subpop fitness of generation " + evaluatedStr + bestSubpop.getFitness().fitnessToStringForHumans());
		}

		// Print the best fitness of an individual per subpopulation.
		for (int s = 0; s < state.population.subpops.length; s++) {
			MLSSubpopulation subpop = (MLSSubpopulation) state.population.subpops[s];

			if (doGeneration) {
				state.output.println("Subpopulation " + s + ": " + subpop.getFitness().fitnessToStringForHumans(), statisticsLog);
				bestOfSubpop[s].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				String evaluatedStr;

				if (bestOfSubpop[s].evaluated) {
					evaluatedStr = "";
				} else {
					evaluatedStr = "(evaluated flag not set): ";
				}

				state.output.message("Subpop " + s + " best fitness of generation: " + evaluatedStr + bestOfSubpop[s].fitness.fitnessToStringForHumans());
			}
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);

		// Print out the best individuals of each subpopulation.
		if (doFinal) {
			state.output.println("\nBest Individual of Run:", statisticsLog);
		}
		for (int s = 0; s < state.population.subpops.length; s++) {
			if (doFinal) {
				state.output.println("Subpopulation " + s + ":", statisticsLog);
				bestIndsOfRun[s].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				state.output.message("Subpop " + s + " best fitness of run: " + bestIndsOfRun[s].fitness.fitnessToStringForHumans());
			}
		}

		// Print out the individuals making up the best subpopulation.

		// TODO now its not printing out all of the individuals.
		if (doFinal) {
			state.output.println("\nBest Subpopulation of Run: " + bestSubpopIndexOfRun + ", Fitness: " + bestSubpopOfRun.getFitness().fitnessToStringForHumans(), statisticsLog);

			System.out.println("Number of individuals: " + bestSubpopOfRun.individuals.length);

			for (int i = 0; i < bestSubpopOfRun.individuals.length; i++) {
				state.output.println("Individual " + i, statisticsLog);
				bestSubpopOfRun.individuals[i].printIndividualForHumans(state, statisticsLog);
			}
		}
	}

}
