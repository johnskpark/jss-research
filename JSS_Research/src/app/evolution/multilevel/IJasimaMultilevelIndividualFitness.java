package app.evolution.multilevel;

import java.util.Map;

import app.evolution.IJasimaFitness;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelIndividualFitness extends IJasimaFitness {

	/**
	 * TODO javadoc.
	 * @param expIndex
	 * @param results
	 */
	public void accumulateFitness(int expIndex, Map<String, Object> results);

}
