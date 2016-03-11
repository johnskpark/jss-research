package app.evolution.coop.statistics;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

// TODO this will now need to get the outputs of the individual rules and the grouped rules,
// similar to the format that I used for Multilevel
public class JasimaCoopStatistics extends SimpleStatistics {

	private static final long serialVersionUID = 8464431576658975312L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// TODO
	}

	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		// Carry out the statistics
		// TODO
	}
}
