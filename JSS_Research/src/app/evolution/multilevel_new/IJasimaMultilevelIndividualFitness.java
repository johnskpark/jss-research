package app.evolution.multilevel_new;

import java.util.Map;

import app.evolution.IJasimaFitness;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelIndividualFitness extends IJasimaFitness<JasimaMultilevelIndividual> {

	/**
	 * TODO javadoc.
	 * @param expIndex
	 * @param results
	 */
	public void accumulateFitness(int expIndex, Map<String, Object> results, double referenceStat);

}
