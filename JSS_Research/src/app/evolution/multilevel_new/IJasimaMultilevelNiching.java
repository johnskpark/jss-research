package app.evolution.multilevel_new;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelNiching extends IJasimaNiching {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param tracker
	 * @param group
	 */
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveDecisionTracker tracker,
			final MLSSubpopulation group);

}
