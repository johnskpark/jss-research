package app.evolution.grouped.grouping;

import app.evolution.grouped.JasimaGroupedIndividual;
import app.evolution.grouped.IJasimaGrouping;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class PopGrouping implements IJasimaGrouping {

	private static final long serialVersionUID = 6709369554204377046L;

	public static final int NUM_TRIALS = 1;

	private Subpopulation population;
	private JasimaGroupedIndividual[] ensemble = new JasimaGroupedIndividual[1];

	private JasimaGroupedIndividual bestEnsemble = null;
	private KozaFitness bestEnsembleFitness = new KozaFitness();

	private JasimaGroupedIndividual bestEnsembleOfGeneration = null;
	private KozaFitness bestEnsembleOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(EvolutionState state, Parameter base) {
		bestEnsembleFitness.setStandardizedFitness(state, Double.MAX_VALUE);
		bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
	}

	@Override
	public void groupIndividuals(EvolutionState state, int threadnum) {
		population = state.population.subpops[0];
		GPIndividual[] indArray = new GPIndividual[population.individuals.length];

		for (int i = 0; i < population.individuals.length; i++) {
			indArray[i] = (GPIndividual) population.individuals[i];
		}

		ensemble[0] = new JasimaGroupedIndividual(indArray);
	}

	@Override
	public int getGroupSize() {
		return population.individuals.length;
	}

	@Override
	public int getNumTrials() {
		return NUM_TRIALS;
	}

	@Override
	public boolean isIndEvaluated() {
		return false;
	}

	@Override
	public boolean isGroupEvaluated() {
		return false;
	}

	@Override
	public JasimaGroupedIndividual[] getGroups(Individual ind) {
		return ensemble;
	}

	@Override
	public JasimaGroupedIndividual getBestGroup() {
		return bestEnsemble;
	}

	@Override
	public KozaFitness getBestGroupFitness() {
		return bestEnsembleFitness;
	}

	@Override
	public JasimaGroupedIndividual getBestGroupForGeneration() {
		return bestEnsembleOfGeneration;
	}

	@Override
	public KozaFitness getBestGroupForGenerationFitness() {
		return bestEnsembleOfGenerationFitness;
	}

	@Override
	public void updateFitness(EvolutionState state, JasimaGroupedIndividual indGroup,
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
