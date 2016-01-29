package app.evolution.multilevel_new;

import java.util.List;
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
	public void accumulateFitness(Individual ind,
			int expIndex,
			Map<String, Object> results,
			double referenceStat);

	/**
	 * TODO javadoc.
	 * @param listener
	 */
	public void addListener(IJasimaMultilevelFitnessListener listener);

	/**
	 * TODO javadoc.
	 */
	public List<Double> getInstanceStats();
}
