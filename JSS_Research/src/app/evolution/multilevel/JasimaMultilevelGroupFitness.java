package app.evolution.multilevel;

import app.evolution.AbsJasimaFitness;
import ec.multilevel_new.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class JasimaMultilevelGroupFitness extends AbsJasimaFitness<MLSSubpopulation> {

	/**
	 * TODO javadoc.
	 * @param listener
	 */
	public abstract void addListener(IJasimaMultilevelFitnessListener listener);

	/**
	 * TODO javadoc.
	 */
	public abstract void clearListeners();

}
