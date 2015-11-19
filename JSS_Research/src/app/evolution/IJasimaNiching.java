package app.evolution;

import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;

public interface IJasimaNiching<T extends JasimaReproducible> {

	// FIXME This too.
	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker, final T individual);

	public void clear();

}
