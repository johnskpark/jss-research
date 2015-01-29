package jss.evolution.grouping;

import jss.evolution.IGroupedIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class PopulationGroupedIndividual implements IGroupedIndividual {

	private static final long serialVersionUID = 350161581678368147L;

	private Subpopulation population;
	private GPIndividual[][] ensemble;

	private GPIndividual[] bestEnsemble = null;
	private KozaFitness bestEnsembleFitness = new KozaFitness();

	private GPIndividual[] bestEnsembleOfGeneration = null;
	private KozaFitness bestEnsembleOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		population = state.population.subpops[0];

		ensemble = new GPIndividual[1][population.individuals.length];
		for (int i = 0; i < population.individuals.length; i++) {
			ensemble[0][i] = (GPIndividual) population.individuals[i];
		}

		bestEnsembleFitness.setStandardizedFitness(state, Double.MAX_VALUE);
		bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

	@Override
	public void groupIndividuals(final EvolutionState state, final int threadnum) {
	}

	@Override
	public GPIndividual[][] getGroups(Individual ind) {
		return ensemble;
	}

	@Override
	public GPIndividual[] getBestGroup() {
		return bestEnsemble;
	}

	@Override
	public KozaFitness getBestGroupFitness() {
		return bestEnsembleFitness;
	}

	@Override
	public GPIndividual[] getBestGroupForGeneration() {
		return bestEnsembleOfGeneration;
	}

	@Override
	public KozaFitness getBestGroupForGenerationFitness() {
		return bestEnsembleOfGenerationFitness;
	}

	@Override
	public void updateFitness(final EvolutionState state,
			final GPIndividual[] indGroup,
			final double fitness) {
		// Update the best group of generation.
		if (bestEnsembleOfGenerationFitness.standardizedFitness() > fitness) {
			bestEnsembleOfGeneration = indGroup;
			bestEnsembleOfGenerationFitness.setStandardizedFitness(state, fitness);
		}

		// Update the overall best group.
		if (bestEnsembleOfGenerationFitness.betterThan(bestEnsembleFitness)) {
			bestEnsemble = bestEnsembleOfGeneration;
			bestEnsembleFitness.setStandardizedFitness(state, bestEnsembleOfGenerationFitness.standardizedFitness());
		}
	}

	@Override
	public void clearForGeneration(final EvolutionState state) {
		bestEnsembleOfGeneration = null;
		bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);

	}

}
