package app.evolution.multilevel_new;

import app.evolution.IJasimaNiching;
import app.tracker.JasimaEvolveDecisionTracker;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

public interface IJasimaMultilevelNiching extends IJasimaNiching {

	public void adjustFitness(final EvolutionState state, final JasimaEvolveDecisionTracker tracker, final MLSSubpopulation group);

}
