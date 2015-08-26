package app.evolution.multilevel.fitness;

import java.util.Map;

import app.evolution.IJasimaTracker;
import app.evolution.multilevel.IJasimaMultilevelGroupFitness;
import app.evolution.multilevel.MLSSubpopulation;
import ec.EvolutionState;
import ec.Individual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MultilevelGroupTWTFitness implements IJasimaMultilevelGroupFitness {

	@Override
	public void accumulateFitness(int expIndex,
			MLSSubpopulation subpop,
			Map<String, Object> results,
			IJasimaTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFitness(EvolutionState state,
			MLSSubpopulation subpop,
			boolean[] updateFitness,
			boolean shouldSetContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
