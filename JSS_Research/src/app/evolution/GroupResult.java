package app.evolution;

import ec.gp.GPIndividual;

public class GroupResult {

	private GPIndividual ind;
	private double fitness;

	public GroupResult(GPIndividual ind, double fitness) {
		this.ind = ind;
		this.fitness = fitness;
	}

	public GPIndividual getInd() {
		return ind;
	}

	public double getFitness() {
		return fitness;
	}
}
