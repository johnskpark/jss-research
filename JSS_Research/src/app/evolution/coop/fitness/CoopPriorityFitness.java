package app.evolution.coop.fitness;

import jasima.core.util.Pair;

import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import app.evolution.coop.IJasimaCoopFitness;

public class CoopPriorityFitness implements IJasimaCoopFitness {

	@Override
	public void loadIndividuals(Individual[] inds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accumulateObjectiveFitness(Individual[] inds,
			Map<String, Object> results) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accumulateDiversityFitness(
			Pair<GPIndividual, Double>[] groupResults) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTrialFitness(EvolutionState state, Individual[] inds,
			boolean[] updateFitness, boolean shouldSetContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDiversityFitness(EvolutionState state, Individual[] inds,
			boolean[] updateFitness) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObjectiveFitness(EvolutionState state, Individual[] inds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
