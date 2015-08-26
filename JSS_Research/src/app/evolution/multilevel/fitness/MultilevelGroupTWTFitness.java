package app.evolution.multilevel.fitness;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import app.evolution.IJasimaTracker;
import app.evolution.multilevel.IJasimaMultilevelGroupFitness;

public class MultilevelGroupTWTFitness implements IJasimaMultilevelGroupFitness {

	@Override
	public void loadIndividuals(GPIndividual[] inds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accumulateFitness(int expIndex,
			GPIndividual[] gpInds,
			Map<String, Object> results,
			IJasimaTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFitness(EvolutionState state,
			Subpopulation subpop,
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
