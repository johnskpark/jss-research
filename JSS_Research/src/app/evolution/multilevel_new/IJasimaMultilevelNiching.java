package app.evolution.multilevel_new;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelNiching extends IJasimaNiching<MLSSubpopulation> {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param tracker
	 * @param group
	 */
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveExperimentTracker tracker,
			final MLSSubpopulation group);

}
