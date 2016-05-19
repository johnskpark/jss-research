package app.evolution;

import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Setup;

public interface IJasimaNiching<T extends JasimaReproducible> extends Setup {

	public void adjustFitness(final EvolutionState state, final JasimaExperimentTracker tracker, final T individual);

	public void clear();

}
