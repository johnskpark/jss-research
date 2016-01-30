package app.evolution.multilevel_new;

import ec.Individual;

public interface IJasimaMultilevelFitnessListener {

	/**
	 * TODO javadoc.
	 * @param type
	 * @param index
	 * @param fitness
	 */
	public void addFitness(int type, int index, double fitness);

	/**
	 * TODO javadoc.
	 * @param index
	 * @param type
	 * @param inds
	 * @param distances
	 */
	public void addDiversity(int type, int index, Individual[] inds, double[] distances);
	
}
