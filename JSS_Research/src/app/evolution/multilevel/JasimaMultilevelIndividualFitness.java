package app.evolution.multilevel;

import app.evolution.JasimaFitnessBase;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class JasimaMultilevelIndividualFitness extends JasimaFitnessBase<JasimaMultilevelIndividual> {

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
