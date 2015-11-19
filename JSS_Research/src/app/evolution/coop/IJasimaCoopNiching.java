package app.evolution.coop;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;

public interface IJasimaCoopNiching extends IJasimaNiching<JasimaCoopIndividual> {

	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker, final JasimaCoopIndividual inds);

}
