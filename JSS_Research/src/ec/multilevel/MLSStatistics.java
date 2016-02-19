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

	protected int statisticsLog = 0; // stdout

	// Statistics for the current generation.

	private Individual bestIndOfGen = null;
	private Individual worstIndOfGen = null;

	private Individual[] bestIndsFromGroupsOfGen = null;
	private Individual[] worstIndsFromGroupsOfGen = null;

	private MLSSubpopulation bestGroupOfGen = null;
	private int bestGroupIndexOfGen = -1;

	private MLSSubpopulation worstGroupOfGen = null;
	private int worstGroupIndexOfGen = -1;

	// Statistics for the entire run.

	private Individual bestIndOfRun = null;
	private Individual[] bestIndsFromGroupsOfRun = null;

	private MLSSubpopulation bestGroupOfRun = null;
	private int bestGroupIndexOfRun = -1;

	private boolean compress;
	private boolean doFinal;
	private boolean doGeneration;
	private boolean doMessage;

	@Override
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

	// Getters associated with the printing.

	public boolean isCompressed() {
		return compress;
	}

	public boolean doFinal() {
		return doFinal;
	}

	public boolean doGeneration() {
		return doGeneration;
	}

	public boolean doMessage() {
		return doMessage;
	}

	// Getters associated with the best and worst individual and group statistics for the current generation.

	public Individual getBestIndividualOfGen() {
		return bestIndOfGen;
	}

	public Individual getWorstIndividualOfGen() {
		return worstIndOfGen;
	}

	public Individual[] getBestIndsFromGroupsOfGen() {
		return bestIndsFromGroupsOfGen;
	}

	public Individual[] getWorstIndsFromGroupsOfGen() {
		return worstIndsFromGroupsOfGen;
	}

	public MLSSubpopulation getBestGroupOfGen() {
		return bestGroupOfGen;
	}

	public int getBestGroupIndexOfGen() {
		return bestGroupIndexOfGen;
	}

	public MLSSubpopulation getWorstGroupOfGen() {
		return worstGroupOfGen;
	}

	public int getWorstGroupIndexofGen() {
		return worstGroupIndexOfGen;
	}

	// Getters associated with the best individual and group statistics for the entire run.

	public Individual getBestIndividualOfRun() {
		return bestIndOfRun;
	}

	public Individual[] getBestIndsFromGroupsOfRun() {
		return bestIndsFromGroupsOfRun;
	}

	public MLSSubpopulation getBestGroupOfRun() {
		return bestGroupOfRun;
	}

	public int getBestGroupIndexOfRun() {
		return bestGroupIndexOfRun;
	}

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		// Set up the bestOfRun array. This cannot be carried out in setup,
		// as the number of subpopulation may not have been determined yet.
		bestIndsFromGroupsOfRun = new Individual[state.population.subpops.length];

		bestIndsFromGroupsOfGen = new Individual[state.population.subpops.length];
		worstIndsFromGroupsOfGen = new Individual[state.population.subpops.length];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Reset the best and worst individuals and groups of current generation.
		bestIndOfGen = null;
		worstIndOfGen = null;

		bestGroupOfGen = null;
		worstGroupOfGen = null;

		bestGroupIndexOfGen = -1;
		worstGroupIndexOfGen = -1;
		
		// Print the current generation.
		if (doGeneration) {
			state.output.println("\nGeneration: " + state.generation, statisticsLog);
		}

		// Carry out the statistics for the individuals.
		individualStatistics(state);

		// Carry out the statistics for the groups.
		if (hasGroups(state)) {
			groupStatistics(state);
		} else {
			state.output.message("No groups in generation " + state.generation);
		}
	}

	protected void individualStatistics(final EvolutionState state) {
		// Update the best individual of generation.
		for (int i = 0; i < state.population.subpops[0].individuals.length; i++) {
			Individual ind = state.population.subpops[0].individuals[i];

			if (bestIndOfGen == null || ind.fitness.betterThan(bestIndOfGen.fitness)) {
				bestIndOfGen = ind;
			}

			if (worstIndOfGen == null || worstIndOfGen.fitness.betterThan(ind.fitness)) {
				worstIndOfGen = ind;
			}
		}

		// Update the best individual of the run.
		if (bestIndOfRun == null || bestIndOfGen.fitness.betterThan(bestIndOfRun.fitness)) {
			bestIndOfRun = (Individual) bestIndOfGen.clone();
		}

		// Print the best individual's fitness to stdout.
		if (doGeneration) {
			String evaluatedStr = (bestIndOfGen.evaluated) ? " " : " (evaluated flag not set): ";
			state.output.message("Best individual of generation: " + evaluatedStr + bestIndOfGen.fitness.fitnessToStringForHumans());
		}

		// Print the best individual and its fitness to output file.
		if (doGeneration) {
			state.output.println("Best Individual: ", statisticsLog);
			bestIndOfGen.printIndividualForHumans(state, statisticsLog);
		}
	}

	protected boolean hasGroups(final EvolutionState state) {
		return state.population.subpops.length > 1;
	}

	protected void groupStatistics(final EvolutionState state) {
		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];

			for (int i = 0; i < group.individuals.length; i++) {
				// Update the best indivdual of group of generation.
				if (bestIndsFromGroupsOfGen[g] == null || group.individuals[i].fitness.betterThan(bestIndsFromGroupsOfGen[g].fitness)) {
					bestIndsFromGroupsOfGen[g] = group.individuals[i];
				}

				// Update the worst individuals of groups of generation.
				if (worstIndsFromGroupsOfGen[g] == null || worstIndsFromGroupsOfGen[g].fitness.betterThan(group.getFitness())) {
					worstIndsFromGroupsOfGen[g] = group.individuals[i];
				}
			}

			// Update the best group of generation.
			if (bestGroupOfGen == null || group.getFitness().betterThan(bestGroupOfGen.getFitness())) {
				bestGroupOfGen = group;
				bestGroupIndexOfGen = g;
			}

			// Update the worst group of generation.
			if (worstGroupOfGen == null || worstGroupOfGen.getFitness().betterThan(group.getFitness())) {
				worstGroupOfGen = group;
				worstGroupIndexOfGen = g;
			}

			// Update the best individuals of groups of the run.
			if (bestIndsFromGroupsOfRun[g] == null || bestIndsFromGroupsOfGen[g].fitness.betterThan(bestIndsFromGroupsOfRun[g].fitness)) {
				bestIndsFromGroupsOfRun[g] = (Individual) bestIndsFromGroupsOfGen[g].clone();
			}
		}

		// Update the best group of the run.
		if (bestGroupOfRun == null || bestGroupOfGen.getFitness().betterThan(bestGroupOfRun.getFitness())) {
			bestGroupOfRun = (MLSSubpopulation) bestGroupOfGen.emptyClone();

			for (int i = 0; i < bestGroupOfGen.individuals.length; i++) {
				bestGroupOfRun.individuals[i] = (Individual) bestGroupOfGen.individuals[i].clone();
			}

			bestGroupIndexOfRun = bestGroupIndexOfGen;
		}

		// Print the best fitness of a group.
		if (doGeneration) {
			String evaluatedStr = (bestGroupOfGen.isEvaluated()) ? " " : " (evaluated flag not set): ";

			state.output.message("Best group of generation: " + bestGroupIndexOfGen + evaluatedStr + bestGroupOfGen.getFitness().fitnessToStringForHumans());
		}

		// Print the best fitness of an individual per group.
		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];

			if (doGeneration) {
				state.output.println("Group " + g + " Fitness: " + group.getFitness().fitnessToStringForHumans() + ", Size: " + group.individuals.length, statisticsLog);
				bestIndsFromGroupsOfGen[g].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				String evaluatedStr = (bestIndsFromGroupsOfGen[g].evaluated) ? "" : "(evaluated flag not set): ";

				state.output.message("Group " + g + " fitness: " + group.getFitness().fitnessToStringForHumans() + ", Size: " + group.individuals.length + ", Individual: " + evaluatedStr + bestIndsFromGroupsOfGen[g].fitness.fitnessToStringForHumans());
			}
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);

		finalIndividualStatistics(state, result);
		finalGroupStatistics(state, result);
	}

	protected void finalIndividualStatistics(final EvolutionState state, final int result) {

		super.finalStatistics(state, result);

		// Print out the best individual.
		if (doFinal) {
			state.output.println("\nBest Individual of Run: " , statisticsLog);
			bestIndOfRun.printIndividualForHumans(state, statisticsLog);
		}
	}

	protected void finalGroupStatistics(final EvolutionState state, final int result) {
		// Print out the best individuals of each group.
		if (doFinal) {
			state.output.println("\nBest Individuals in each Groups of Run: ", statisticsLog);
		}

		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];
			Individual ind = getBestIndsFromGroupsOfGen()[g];

			if (doFinal) {
				state.output.println("Group " + g + ": ", statisticsLog);
				ind.printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				state.output.message("Group " + g + " best fitness of run: " + group.getFitness().fitnessToStringForHumans());
			}
		}

		// Print out the individuals making up the best group.
		if (doFinal) {
			state.output.println("\nBest Group of Run: " + bestGroupIndexOfRun + ", Fitness: " + bestGroupOfRun.getFitness().fitnessToStringForHumans(), statisticsLog);

			for (int i = 0; i < bestGroupOfRun.individuals.length; i++) {
				state.output.println("Individual " + i, statisticsLog);
				bestGroupOfRun.individuals[i].printIndividualForHumans(state, statisticsLog);
			}
		}
	}

}
