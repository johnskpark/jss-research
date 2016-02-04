package app.evolution.multilevel;

import java.util.List;
import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.multilevel_new.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaMultilevelGroupFitness extends IJasimaFitness<MLSSubpopulation> {

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
	 * @param listener
	 */
	public void addListener(IJasimaMultilevelFitnessListener listener);

	/**
	 * TODO javadoc.
	 */
	public void clearListeners();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<Double> getInstanceStats();
}
