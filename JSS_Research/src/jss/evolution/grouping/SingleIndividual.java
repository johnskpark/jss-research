package jss.evolution.grouping;

import jss.evolution.IGroupedIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class SingleIndividual implements IGroupedIndividual {

	private static final long serialVersionUID = 6783740636522782222L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// TODO Auto-generated method stub

	}

	@Override
	public void groupIndividuals(final EvolutionState state, final int threadnum) {
		// TODO Auto-generated method stub

	}

	@Override
	public GPIndividual[] getBestGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GPIndividual[] getBestGroupForGeneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearForGeneration(final EvolutionState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public KozaFitness getBestGroupFitness() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KozaFitness getBestGroupForGenerationFitness() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GPIndividual[][] getGroups(Individual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateFitness(EvolutionState state, GPIndividual[] indGroup,
			double fitness) {
		// TODO Auto-generated method stub

	}

}
