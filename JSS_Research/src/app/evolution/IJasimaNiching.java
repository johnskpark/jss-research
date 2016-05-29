package app.evolution;

import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

public interface IJasimaNiching<T extends JasimaReproducible> extends Setup {

	public void adjustFitness(final EvolutionState state,
			final JasimaExperimentTracker<Individual> tracker,
			final T individual,
			final AbsGPPriorityRule solver);

	public void clear();

}
