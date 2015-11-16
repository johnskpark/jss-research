package app.evolution.multilevel_new;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

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
			Map<String, Object> results);

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
