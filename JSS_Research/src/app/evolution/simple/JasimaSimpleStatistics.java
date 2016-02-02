package app.evolution.simple;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;
import jasima.core.statistics.SummaryStat;

public class JasimaSimpleStatistics extends SimpleStatistics {

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		SummaryStat indStat = new SummaryStat();

		// Collect the standarded fitnesses of the individuals.

	}

	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		// TODO
	}

}
