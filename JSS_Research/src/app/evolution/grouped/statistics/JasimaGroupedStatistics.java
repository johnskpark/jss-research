package app.evolution.grouped.statistics;

import app.evolution.grouped.JasimaGroupedIndividual;
import app.evolution.grouped.IJasimaGrouping;
import app.evolution.grouped.JasimaGroupedProblem;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;

public class JasimaGroupedStatistics extends SimpleStatistics {

	private static final long serialVersionUID = -9158971059437045071L;

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		// TODO need to make it print out the trees in a more organised manner.
		super.postEvaluationStatistics(state);

		JasimaGroupedProblem problem = (JasimaGroupedProblem) state.evaluator.p_problem;

		IJasimaGrouping individualGrouping = problem.getIndGrouping();

		// Print out the best ensemble over the specific generation that was evaluated.
		state.output.println("Ensemble:", statisticslog);

		JasimaGroupedIndividual group = individualGrouping.getBestGroupForGeneration();
		GPIndividual[] inds = group.getInds();
		for (int i = 0; i < inds.length; i++) {
			inds[i].printIndividualForHumans(state, statisticslog);
		}
	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		// TODO need to make it print out the trees in a more organised manner.
		super.finalStatistics(state, result);

		JasimaGroupedProblem problem = (JasimaGroupedProblem) state.evaluator.p_problem;

		IJasimaGrouping individualGrouping = problem.getIndGrouping();

		// Print out the best ensemble over all generation that was evaluated.
		state.output.println("Ensemble:", statisticslog);

		JasimaGroupedIndividual group = individualGrouping.getBestGroupForGeneration();
		GPIndividual[] inds = group.getInds();
		for (int i = 0; i < inds.length; i++) {
			inds[i].printIndividualForHumans(state, statisticslog);
		}
	}

}
