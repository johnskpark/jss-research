package app.evolution.multilevel;

import app.evolution.JasimaFitnessBase;
import ec.multilevel.MLSSubpopulation;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class JasimaMultilevelGroupFitness extends JasimaFitnessBase<MLSSubpopulation> {

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
