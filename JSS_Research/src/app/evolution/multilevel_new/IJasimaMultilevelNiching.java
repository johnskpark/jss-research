package app.evolution.multilevel_new;

import app.evolution.IJasimaNiching;
import ec.multilevel_new.MLSSubpopulation;

public interface IJasimaMultilevelNiching extends IJasimaNiching<MLSSubpopulation> {

	/**
	 * TODO javadoc.
	 * @param listener
	 */
	public void addListener(IJasimaMultilevelFitnessListener listener);

	/**
	 * TODO javadoc.
	 */
	public void clearListeners();

}
