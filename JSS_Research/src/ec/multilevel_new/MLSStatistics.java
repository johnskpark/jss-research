package ec.multilevel_new;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.gp.koza.KozaFitness;
import ec.util.Output;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */

// TODO currently its printing out that the best individual is W / PR
// (which is clearly not the case), so need to fix this.
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

	private Individual bestIndOfRun = null;
	private Individual bestIndLastGen = null;

	private Individual[] bestIndsOfGroupsOfRun = null;
	private Individual[] bestIndsOfGroupsLastGen = null;

	private MLSSubpopulation bestGroupOfRun = null;
	private MLSSubpopulation bestGroupLastGen = null;

	private int bestGroupIndexLastGen = -1;
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

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		// Set up the bestOfRun array. This cannot be carried out in setup,
		// as the number of subpopulation may not have been determined yet.
		bestIndsOfGroupsOfRun = new Individual[state.population.subpops.length];
		bestIndsOfGroupsLastGen = new Individual[state.population.subpops.length];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Print the current generation.
		if (doGeneration) {
			state.output.println("\nGeneration: " + state.generation, statisticsLog);
		}

		// Carry out the statistics for the separate components.
		individualStatistics(state);
		groupStatistics(state);

	}

	protected void individualStatistics(final EvolutionState state) {
		Individual bestInd = null;
		Individual worstInd = null;

		double sumFitness = 0.0;
		double numEvaluated = 0.0;

		double bestFitness = 0.0;
		double worstFitness = 0.0;
		double avgFitness = 0.0;

		// Update the best individual of generation.
		for (int i = 0; i < state.population.subpops[0].individuals.length; i++) {
			Individual ind = state.population.subpops[0].individuals[i];

			if (bestInd == null || ind.fitness.betterThan(bestInd.fitness)) {
				bestInd = ind;

				bestFitness = ((KozaFitness) bestInd.fitness).standardizedFitness();
			}

			if (worstInd == null || worstInd.fitness.betterThan(ind.fitness)) {
				worstInd = ind;

				worstFitness = ((KozaFitness) worstInd.fitness).standardizedFitness();
			}

			// Fuck it, all everyone uses is KozaFitness anyways.
			if (ind.evaluated) {
				sumFitness += ((KozaFitness) ind.fitness).standardizedFitness();
				numEvaluated++;
			}
		}

		avgFitness = sumFitness / numEvaluated;

		// Update the best individual of the run.
		if (bestIndOfRun == null || bestInd.fitness.betterThan(bestIndOfRun.fitness)) {
			bestIndOfRun = (Individual) bestInd.clone();
		}

		// Update the best individual of last gen.
		bestIndLastGen = (Individual) bestInd.clone();

		// Print the best individual's fitness to stdout.
		if (doGeneration) {
			String evaluatedStr = (bestInd.evaluated) ? " " : " (evaluated flag not set): ";
			state.output.message("Best individual of generation: " + evaluatedStr + bestInd.fitness.fitnessToStringForHumans());
		}

		// Print the best individual and its fitness to output file.
		if (doGeneration) {
			state.output.println("Best Individual: ", statisticsLog);
			bestInd.printIndividualForHumans(state, statisticsLog);
		}

		// Print out the average individual's fitnesses.
		if (doGeneration) {
			state.output.println("Best Individual Fitness: " + bestFitness, statisticsLog);
			state.output.println("Worst Individual Fitness: " + worstFitness, statisticsLog);
			state.output.println("Average Individual Fitness: " + avgFitness, statisticsLog);
		}
	}

	protected void groupStatistics(final EvolutionState state) {
		// Immediately return if there is no groups.
		if (state.population.subpops.length == 1) {
			state.output.message("No groups in generation " + state.generation);
			return;
		}

		Individual[] bestOfGroups = new Individual[state.population.subpops.length - 1];

		MLSSubpopulation bestGroup = null;
		MLSSubpopulation worstGroup = null;
		int bestGroupIndex = -1;

		double sumFitness = 0.0;
		double numEvaluated = 0.0;

		double bestFitness = 0.0;
		double worstFitness = 0.0;
		double avgFitness = 0.0;

		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];

			// Update the best indivdual of group of generation.
			for (int i = 0; i < group.individuals.length; i++) {
				if (bestOfGroups[g] == null || group.individuals[i].fitness.betterThan(bestOfGroups[g].fitness)) {
					bestOfGroups[g] = group.individuals[i];
				}
			}

			// Update the best group of generation.
			if (bestGroup == null || group.getFitness().betterThan(bestGroup.getFitness())) {
				bestGroup = group;
				bestGroupIndex = g;

				bestFitness = ((KozaFitness) bestGroup.getFitness()).standardizedFitness();
			}

			// Update the worst group of generation.
			if (worstGroup == null || worstGroup.getFitness().betterThan(group.getFitness())) {
				worstGroup = group;

				worstFitness = ((KozaFitness) worstGroup.getFitness()).standardizedFitness();
			}

			// Update the best individuals of groups of the run.
			if (bestIndsOfGroupsOfRun[g] == null || bestOfGroups[g].fitness.betterThan(bestIndsOfGroupsOfRun[g].fitness)) {
				bestIndsOfGroupsOfRun[g] = (Individual) bestOfGroups[g].clone();
			}

			// Update the best individuals of groups last gen.
			bestIndsOfGroupsLastGen[g] = (Individual) bestOfGroups[g].clone();

			// Fuck it, all everyone uses is KozaFitness anyways.
			if (group.isEvaluated()) {
				sumFitness += ((KozaFitness) group.getFitness()).standardizedFitness();
				numEvaluated++;
			}
		}

		avgFitness = sumFitness / numEvaluated;

		// Update the best group of the run.
		if (bestGroupOfRun == null || bestGroup.getFitness().betterThan(bestGroupOfRun.getFitness())) {
			bestGroupOfRun = (MLSSubpopulation) bestGroup.emptyClone();

			for (int i = 0; i < bestGroup.individuals.length; i++) {
				bestGroupOfRun.individuals[i] = (Individual) bestGroup.individuals[i].clone();
			}

			bestGroupIndexOfRun = bestGroupIndex;
		}

		// Update the best group last gen.
		bestGroupLastGen = (MLSSubpopulation) bestGroup.emptyClone();

		for (int i = 0; i < bestGroup.individuals.length; i++) {
			bestGroupLastGen.individuals[i] = (Individual) bestGroup.individuals[i].clone();
		}

		bestGroupIndexLastGen = bestGroupIndex;

		// Print the best fitness of a group.
		if (doGeneration) {
			String evaluatedStr = (bestGroup.isEvaluated()) ? " " : " (evaluated flag not set): ";

			state.output.message("Best group of generation: " + bestGroupIndex + evaluatedStr + bestGroup.getFitness().fitnessToStringForHumans());
		}

		// Print the best fitness of an individual per group.
		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];

			if (doGeneration) {
				state.output.println("Group " + g + " Fitness: " + group.getFitness().fitnessToStringForHumans() + ", Size: " + group.individuals.length, statisticsLog);
				bestOfGroups[g].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				String evaluatedStr = (bestOfGroups[g].evaluated) ? "" : "(evaluated flag not set): ";

				state.output.message("Group " + g + " fitness: " + group.getFitness().fitnessToStringForHumans() + ", Size: " + group.individuals.length + ", Individual: " + evaluatedStr + bestOfGroups[g].fitness.fitnessToStringForHumans());
			}
		}

		// Print out the best and average group's fitnesses.
		if (doGeneration) {
			state.output.println("Best Group Fitness: " + bestFitness, statisticsLog);
			state.output.println("Worst Group Fitness: " + worstFitness, statisticsLog);
			state.output.println("Average Group Fitness: " + avgFitness, statisticsLog);
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);

		// Print out the best individual.
		if (doFinal) {
			state.output.println("\nBest Individual of Run: " , statisticsLog);
			bestIndOfRun.printIndividualForHumans(state, statisticsLog);
		}

		// Print out the best individuals of each group.
		if (doFinal) {
			state.output.println("\nBest Individuals in each Groups of Run: ", statisticsLog);
		}

		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			if (doFinal) {
				state.output.println("Group " + g + ": ", statisticsLog);
				bestIndsOfGroupsOfRun[g].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage && !silentPrint) {
				state.output.message("Group " + g + " best fitness of run: " + bestIndsOfGroupsOfRun[g].fitness.fitnessToStringForHumans());
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
