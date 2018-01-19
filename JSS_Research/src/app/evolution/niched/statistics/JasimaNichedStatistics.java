package app.evolution.niched.statistics;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;

public class JasimaNichedStatistics extends SimpleStatistics {

	private static final long serialVersionUID = -2627954270593351497L;

	// TODO this needs to calculate what individuals are what in the archive.


	@Override
    public void postEvaluationStatistics(final EvolutionState state) {
		// TODO can't do super.postEvaluationStatistics.

		// TODO

    	// TODO write down the after generation statistics here.

		// TODO this part also needs to carry out the niching procedure where it sets the individual's fitnesses.


		throw new RuntimeException("TODO");
    }


	@Override
    public void finalStatistics(final EvolutionState state, final int result) {
    	// TODO write down the final statistics here.

		throw new RuntimeException("TODO");
    }

}
