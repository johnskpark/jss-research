package app.evolution;

import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;

public interface IJasimaNiching<T extends JasimaReproducible> {

	// FIXME This too.
	public void adjustFitness(final EvolutionState state, final JasimaEvolveExperimentTracker tracker, final T individual);

}
