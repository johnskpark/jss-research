package jss.evolution;

import ec.EvolutionState;
import ec.Individual;
import ec.Setup;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

public interface IGroupedIndividual extends Setup {

	// TODO Add in the documentation later on.

	public void groupIndividuals(final EvolutionState state, final int threadnum);

	public GPIndividual[][] getGroups(final Individual ind);

	public GPIndividual[] getBestGroup();

	public KozaFitness getBestGroupFitness();

	public GPIndividual[] getBestGroupForGeneration();

	public KozaFitness getBestGroupForGenerationFitness();

	public void updateFitness(final EvolutionState state,
			final GPIndividual[] indGroup,
			final double fitness);

	public void clearForGeneration(final EvolutionState state);

}
