package app.evolution.multilevel_new;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.Individual;

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
	public void accumulateFitness(Individual ind, int expIndex, Map<String, Object> results, double referenceStat);

}
