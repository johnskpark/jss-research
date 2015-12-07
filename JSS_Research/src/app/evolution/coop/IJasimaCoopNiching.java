package app.evolution.coop;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;

public interface IJasimaCoopNiching extends IJasimaNiching<JasimaCoopIndividual> {

	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveExperimentTracker tracker,
			final boolean[] updateFitness,
			final JasimaCoopIndividual individual);
}
