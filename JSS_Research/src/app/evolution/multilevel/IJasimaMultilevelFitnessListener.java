package app.evolution.multilevel;

import ec.Individual;

public interface IJasimaMultilevelFitnessListener {

	public void addFitness(int type, int index, double fitness);

	public void addDiversity(int type, int index, Individual[] inds, double[] distances);

}
