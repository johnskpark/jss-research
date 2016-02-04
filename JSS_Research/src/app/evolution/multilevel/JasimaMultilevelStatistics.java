package app.evolution.multilevel;

import java.util.ArrayList;
import java.util.List;

import app.util.ArrayFormatter;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.multilevel_new.MLSStatistics;
import ec.multilevel_new.MLSSubpopulation;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaMultilevelStatistics extends MLSStatistics implements IJasimaMultilevelFitnessListener {

	private static final long serialVersionUID = 1831159170439048338L;

	public static final String P_ARRAY_FORMAT = "format";

	public static final String V_BRACKET = "bracket";
	public static final String V_SQUARE = "square-bracket";
	public static final String V_CURLY = "curly-bracket";
	public static final String V_R = "R";

	public static final int INDIVIDUAL_FITNESS = 0;
	public static final int ENSEMBLE_FITNESS = 1;

	private Individual bestIndLastGen = null;

	private Individual[] bestIndsOfGroupsLastGen = null;

	private MLSSubpopulation bestGroupLastGen = null;

	private int bestGroupIndexLastGen = -1;

	private List<SummaryStat> individualFitnesses = new ArrayList<SummaryStat>();
	private List<SummaryStat> ensembleFitnesses = new ArrayList<SummaryStat>();

	private List<Double> instanceDistanceSum = new ArrayList<Double>();
	private List<Integer> instanceDistanceCount = new ArrayList<Integer>();

	private ArrayFormatter arrayFormat;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Determine the format for the arrays.
		String format = state.parameters.getStringWithDefault(base.push(P_ARRAY_FORMAT), null, V_BRACKET);
		if (format.equals(V_BRACKET)) {
			arrayFormat = new ArrayFormatter("(", ")");
		} else if (format.equals(V_SQUARE)) {
			arrayFormat = new ArrayFormatter("[", "]");
		} else if (format.equals(V_CURLY)) {
			arrayFormat = new ArrayFormatter("{", "}");
		} else if (format.equals(V_R)) {
			arrayFormat = new ArrayFormatter("c(", ")");
		} else {
			state.output.warning("JasimaMultilevelNichingStatistics: Unrecognised array format: " + format + ", defaulting to bracket format.");
			arrayFormat = new ArrayFormatter("(", ")");
		}
	}

	@Override
	public void addFitness(int type, int index, double value) {
		// Add the instance statistics.
		if (type == INDIVIDUAL_FITNESS) {
			individualFitnesses.get(index).value(value);
		} else if (type == ENSEMBLE_FITNESS) {
			ensembleFitnesses.get(index).value(value);
		}
	}

	@Override
	public void addDiversity(int type, int index, Individual[] inds, double[] distances) {
		// Add in the instance diversity
		if (type == INDIVIDUAL_FITNESS) {
			addDiversityForIndividuals(index, inds, distances);
		} else if (type == ENSEMBLE_FITNESS) {
			addDiversityForIndividuals(index, inds, distances);
		}
	}

	private void addDiversityForIndividuals(int index, Individual[] inds, double[] distances) {
		for (int i = 0; i < inds.length; i++) {
			double oldInstDist = instanceDistanceSum.get(index);
			double newInstDist = oldInstDist + distances[i];

			instanceDistanceSum.set(index, newInstDist);
			instanceDistanceCount.set(index, instanceDistanceCount.get(index) + 1);
		}
	}

	@Override
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		bestIndsOfGroupsLastGen = new Individual[state.population.subpops.length];

		JasimaMultilevelProblem problem = (JasimaMultilevelProblem) state.evaluator.p_problem;

		int numConfigs = problem.getSimConfig().getNumConfigs();

		// Fill in the fitnesses.
		for (int i = 0; i < numConfigs; i++) {
			individualFitnesses.add(new SummaryStat());
			ensembleFitnesses.add(new SummaryStat());

			instanceDistanceSum.add(0.0);
			instanceDistanceCount.add(0);
		}

		// Have the statistics listen on the fitnesses.
		problem.getIndividualFitness().addListener((JasimaMultilevelStatistics) state.statistics);
		problem.getGroupFitness().addListener((JasimaMultilevelStatistics) state.statistics);
		if (problem.getNiching() != null) {
			problem.getNiching().addListener((JasimaMultilevelStatistics) state.statistics);
		}
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Carry out the statistics for the individual training instances.
		instanceStatistics(state);

		individualFitnesses.clear();
		ensembleFitnesses.clear();

		instanceDistanceSum.clear();
		instanceDistanceCount.clear();

		// Remove the statistics from the listeners.
		// FIXME this should belong outside of the post evaluation statistics.
		JasimaMultilevelProblem problem = (JasimaMultilevelProblem) state.evaluator.p_problem;

		problem.getIndividualFitness().clearListeners();
		problem.getGroupFitness().clearListeners();
		if (problem.getNiching() != null) {
			problem.getNiching().clearListeners();
		}
	}

	protected void individualStatistics(final EvolutionState state) {
		super.individualStatistics(state);

		double sumFitness = 0.0;
		double numEvaluated = 0.0;

		double bestFitness = ((KozaFitness) getBestIndividualOfGen().fitness).standardizedFitness();
		double worstFitness = ((KozaFitness) getWorstIndividualOfGen().fitness).standardizedFitness();
		double avgFitness = 0.0;

		for (int i = 0; i < state.population.subpops[0].individuals.length; i++) {
			Individual ind = state.population.subpops[0].individuals[i];

			// Jasima problems use KozaFitness.
			if (ind.evaluated) {
				sumFitness += ((KozaFitness) ind.fitness).standardizedFitness();
				numEvaluated++;
			}
		}

		avgFitness = sumFitness / numEvaluated;

		// Print out a summary of the individual's fitnesses.
		if (doGeneration()) {
			state.output.println("Best/Avg/Worst Individual Fitness: " + arrayFormat.formatData(bestFitness, worstFitness, avgFitness), statisticsLog);
		}
	}

	protected void groupStatistics(final EvolutionState state) {
		super.groupStatistics(state);

		double sumFitness = 0.0;
		double numEvaluated = 0.0;

		double bestFitness = ((KozaFitness) getBestGroupOfGen().getFitness()).standardizedFitness();
		double worstFitness = ((KozaFitness) getWorstGroupOfGen().getFitness()).standardizedFitness();
		double avgFitness = 0.0;

		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			MLSSubpopulation group = (MLSSubpopulation) state.population.subpops[g + 1];

			// Jasima problems use KozaFitness.
			if (group.isEvaluated()) {
				sumFitness += ((KozaFitness) group.getFitness()).standardizedFitness();
				numEvaluated++;
			}
		}

		avgFitness = sumFitness / numEvaluated;

		// Print out a summary of the group's fitnesses.
		if (doGeneration()) {
			state.output.println("Best/Avg/Worst Group Fitness: " + arrayFormat.formatData(bestFitness, worstFitness, avgFitness), statisticsLog);
		}
	}

	protected void instanceStatistics(final EvolutionState state) {
		// Print out the instance statistics.
		fitnessStatistics(state, "Individual", individualFitnesses);
		fitnessStatistics(state, "Ensemble", ensembleFitnesses);

		state.output.print("Average Distance per Instance: ", statisticsLog);
		for (int i = 0; i < instanceDistanceSum.size(); i++) {
			double avg = instanceDistanceSum.get(i) / instanceDistanceCount.get(i);

			if (i == 0) {
				state.output.print("" + avg, statisticsLog);
			} else {
				state.output.print("," + avg, statisticsLog);
			}
		}
		state.output.println("", statisticsLog);
	}

	protected void fitnessStatistics(final EvolutionState state, String fitnessType, List<SummaryStat> fitnessStats) {
		state.output.print("Best/Average/Worst " + fitnessType + " Fitnesses per Instance: ", statisticsLog);

		for (int i = 0; i < fitnessStats.size(); i++) {
			SummaryStat stat = fitnessStats.get(i);

			if (i != 0) {
				state.output.print(",", statisticsLog);
			}
			state.output.print(arrayFormat.formatData(stat.min(), stat.mean(), stat.max()), statisticsLog);
		}
		state.output.println("", statisticsLog);
	}

	// Instead of printing out the best individual over all generations,
	// print out the best individuals and groups from the last generation.

	@Override
	protected void finalIndividualStatistics(final EvolutionState state, final int result) {
		// Print out the best individual.
		if (doFinal()) {
			state.output.println("\nBest Individual of Run: " , statisticsLog);
			bestIndLastGen.printIndividualForHumans(state, statisticsLog);
		}
	}

	@Override
	protected void finalGroupStatistics(final EvolutionState state, final int result) {
		// Print out the best individuals of each group.
		if (doFinal()) {
			state.output.println("\nBest Individuals in each Groups of Run: ", statisticsLog);
		}

		for (int g = 0; g < state.population.subpops.length - 1; g++) {
			if (doFinal()) {
				state.output.println("Group " + g + ": ", statisticsLog);
				bestIndsOfGroupsLastGen[g].printIndividualForHumans(state, statisticsLog);
			}

			if (doMessage() && !silentPrint) {
				state.output.message("Group " + g + " best fitness of run: " + bestIndsOfGroupsLastGen[g].fitness.fitnessToStringForHumans());
			}
		}

		// Print out the individuals making up the best group.
		if (doFinal()) {
			state.output.println("\nBest Group of Run: " + bestGroupIndexLastGen + ", Fitness: " + bestGroupLastGen.getFitness().fitnessToStringForHumans(), statisticsLog);

			for (int i = 0; i < bestGroupLastGen.individuals.length; i++) {
				state.output.println("Individual " + i, statisticsLog);
				bestGroupLastGen.individuals[i].printIndividualForHumans(state, statisticsLog);
			}
		}
	}

}
