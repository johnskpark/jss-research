package app.evolution.multilevel_new.niching;

import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;
import app.evolution.multilevel_new.IJasimaMultilevelNiching;
import app.tracker.JasimaEvolveDecisionTracker;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MultilevelDecisionNiching implements IJasimaMultilevelNiching {

	// TODO Right, what do I do here?

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveDecisionTracker tracker,
			final MLSSubpopulation group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveDecisionTracker tracker) {
		// FIXME does nothing at the moment.
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
