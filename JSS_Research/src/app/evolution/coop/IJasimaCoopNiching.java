package app.evolution.coop;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaNiching;
import app.tracker.JasimaExperimentTracker;
import ec.EvolutionState;
import ec.Individual;

public interface IJasimaCoopNiching extends IJasimaNiching<JasimaCoopGPIndividual> {

	public void adjustFitness(final EvolutionState state,
			final JasimaExperimentTracker<Individual> tracker,
			final boolean[] updateFitness,
			final JasimaCoopIndividual individual,
			final AbsGPPriorityRule solver);
}
