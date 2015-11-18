package app.evolution.multilevel_new;

import java.util.Map;

import ec.multilevel_new.MLSGPIndividual;
import app.evolution.IJasimaFitness;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelIndividualFitness extends IJasimaFitness<MLSGPIndividual> {

	/**
	 * TODO javadoc.
	 * @param expIndex
	 * @param results
	 */
	public void accumulateFitness(int expIndex, Map<String, Object> results);

}
