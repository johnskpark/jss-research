package app.evolution.grouping;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import app.evolution.IJasimaGrouping;

// TODO this needs more working as well. There is no such thing as
// the best ensemble of generation. It's the best individual of generation.
public class PopGrouping implements IJasimaGrouping {

	private static final long serialVersionUID = 6709369554204377046L;

	private Subpopulation population;
	private GPIndividual[][] ensemble;

	private GPIndividual[] bestEnsemble = null;
	private KozaFitness bestEnsembleFitness = new KozaFitness();

	private GPIndividual[] bestEnsembleOfGeneration = null;
	private KozaFitness bestEnsembleOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(EvolutionState state, Parameter base) {
		bestEnsembleFitness.setStandardizedFitness(state, Double.MAX_VALUE);
		bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

	@Override
	public void groupIndividuals(EvolutionState state, int threadnum) {
		population = state.population.subpops[0];
		ensemble = new GPIndividual[1][population.individuals.length];

		for (int i = 0; i < population.individuals.length; i++) {
			ensemble[0][i] = (GPIndividual) population.individuals[i];
		}
	}

	@Override
	public boolean isIndEvaluated() {
		return true;
	}

	@Override
	public boolean isGroupEvaluated() {
		return true;
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
	public void updateFitness(EvolutionState state, GPIndividual[] indGroup,
			double fitness) {
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
	public void clearForGeneration(EvolutionState state) {
		bestEnsembleOfGeneration = null;
		bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

}
