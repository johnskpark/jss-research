package app.evolution.coop;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;
import ec.Individual;

public interface IJasimaCoopNiching extends IJasimaNiching {

	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker, final Individual[] inds);

}
