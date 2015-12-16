package app.evolution;

import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.Setup;

public interface IJasimaNiching<T extends JasimaReproducible> extends Setup {

	public void adjustFitness(final EvolutionState state, final JasimaEvolveExperimentTracker tracker, final T individual);

}
