package app.evolution;

import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;

public interface IJasimaNiching {

	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker);

	public void clear();

}
