package app.evolution.coop;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;

public interface IJasimaCoopNiching extends IJasimaNiching<JasimaCoopGPIndividual> {

	public void adjustFitness(final EvolutionState state,
			final JasimaExperimentTracker tracker,
			final boolean[] updateFitness,
			final JasimaCoopIndividual individual);
}
