package app.evolution.coop.statistics;

import app.evolution.coop.JasimaCoopProblem;
import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaCoopStatistics extends SimpleStatistics {

	private static final long serialVersionUID = 8464431576658975312L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// The additional statistics required are:
		// - The individual's fitness when applied to the training instances
		// as a single dispatching rule.
		JasimaCoopProblem problem = (JasimaCoopProblem) state.evaluator.p_problem;

		SummaryStat stat = problem.getAllIndStats();
		SummaryStat[] statPerSubpop = problem.getIndStatPerSubpop();

		state.output.message("Best Individual Statistics: " + stat.min());
		state.output.println("Best Individual Statistics: " + stat.min(), statisticslog);
		for (int i = 0; i < statPerSubpop.length; i++) {
			state.output.message("Best Individual Statistics Subpop " + i + ": " + statPerSubpop[i].min());
			state.output.println("Best Individual Statistics Subpop " + i + ": " + statPerSubpop[i].min(), statisticslog);
		}
	}
}
