package app.evolution.multilevel;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaTracker;
import ec.EvolutionState;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelGroupFitness extends IJasimaFitness {

	/**
	 * TODO javadoc.
	 * @param expIndex
	 * @param gpInds
	 * @param results
	 * @param tracker
	 */
	public void accumulateFitness(int expIndex,
			MLSSubpopulation subpop,
			Map<String, Object> results,
			IJasimaTracker tracker);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param subpop
	 * @param updateFitness
	 * @param shouldSetContext
	 */
	public void setFitness(EvolutionState state,
			MLSSubpopulation subpop,
			boolean[] updateFitness,
			boolean shouldSetContext);

}
