package app.evolution;

import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;

public interface IJasimaNiching {

	// FIXME This too.
	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker);

	public void clear();

}
