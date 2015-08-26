package app.evolution.multilevel;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.IJasimaTracker;
import ec.EvolutionState;
import ec.Subpopulation;
import ec.gp.GPIndividual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelGroupFitness extends IJasimaFitness {

	/**
	 * TODO javadoc.
	 * @param inds
	 */
	public void loadIndividuals(GPIndividual[] inds);

	/**
	 * TODO javadoc.
	 * @param expIndex
	 * @param gpInds
	 * @param results
	 * @param tracker
	 */
	public void accumulateFitness(int expIndex,
			GPIndividual[] gpInds,
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
			Subpopulation subpop,
			boolean[] updateFitness,
			boolean shouldSetContext);

}
