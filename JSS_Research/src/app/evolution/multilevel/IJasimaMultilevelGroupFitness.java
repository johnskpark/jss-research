package app.evolution.multilevel;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaTracker;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.gp.GPIndividual;

public interface IJasimaMultilevelGroupFitness extends IJasimaFitness {

	public void loadIndividuals(GPIndividual[] inds);

	public void accumulateFitness(int expIndex,
			GPIndividual[] gpInds,
			Map<String, Object> results,
			IJasimaTracker tracker);

	public void setFitness(EvolutionState state,
			Subpopulation subpop,
			boolean[] updateFitness,
			boolean shouldSetContext);

}
