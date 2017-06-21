package app.evolution.simple;

import app.util.ArrayFormatter;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaSimpleStatistics extends SimpleStatistics {

	private static final long serialVersionUID = 4953168691979397526L;

	public static final String P_ARRAY_FORMAT = "format";

	public static final String V_BRACKET = "bracket";
	public static final String V_SQUARE = "square-bracket";
	public static final String V_CURLY = "curly-bracket";
	public static final String V_R = "R";

	private Individual[] bestIndOfGen = null;
	private Individual[] worstIndOfGen = null;

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
	public void preEvaluationStatistics(final EvolutionState state) {
		super.preEvaluationStatistics(state);

		int numSubpops = state.population.subpops.length;

		bestIndOfGen = new Individual[numSubpops];
		worstIndOfGen = new Individual[numSubpops];
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Collect the standarded fitnesses of the individuals.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			SummaryStat stat = new SummaryStat();

			for (int j = 0; j < subpop.individuals.length; j++) {
				Individual ind = subpop.individuals[j];

				// Jasima problems use KozaFitness.
				if (ind.evaluated) {
					if (bestIndOfGen[i] == null || ind.fitness.betterThan(bestIndOfGen[i].fitness)) {
						bestIndOfGen[i] = ind;
					}

					if (worstIndOfGen[i] == null || worstIndOfGen[i].fitness.betterThan(ind.fitness)) {
						worstIndOfGen[i] = ind;
					}

					stat.value(((KozaFitness) ind.fitness).standardizedFitness());
				}
			}

			double bestFitness = stat.min();
			double worstFitness = stat.max();
			double avgFitness = stat.mean();

			// Print out a summary of the individual's fitnesses.
			if (doGeneration) {
				state.output.println("Subpopulation " + i + " Best/Avg/Worst Individual Fitness: " + arrayFormat.formatData(bestFitness, worstFitness, avgFitness), statisticslog);
			}
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		bypassFinalStatistics(state, result);

		// for now we just print the best fitness of last generation of subpopulation i.
		if (doFinal) { state.output.println("\nBest Individual of Run:", statisticslog); }
		for (int i = 0; i < state.population.subpops.length; i++) {
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
		}


	}

}
