package app.evolution.grouped.fitness;

import jasima.core.util.Pair;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import app.evolution.grouped.GroupedIndividual;
import app.evolution.grouped.IJasimaGroupFitness;

public class PopTWTFitness implements IJasimaGroupFitness {

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void accumulateIndFitness(Individual ind, Map<String, Object> results) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accumulateGroupFitness(Individual ind,
			Map<String, Object> results,
			Pair<GPIndividual, Double>[] groupResults) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIndFitness(EvolutionState state, Individual ind) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupFitness(EvolutionState state, Individual ind,
			GroupedIndividual group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearIndFitness() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearGroupFitness() {
		// TODO Auto-generated method stub

	}

}
