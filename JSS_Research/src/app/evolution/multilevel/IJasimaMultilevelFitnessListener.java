package app.evolution.multilevel;

import java.util.List;

import ec.Individual;

public interface IJasimaMultilevelFitnessListener {

	public void addFitness(int type, int index, double fitness);

	public void addDiversity(int type, int index, List<Individual> inds, double[] distances);

}
