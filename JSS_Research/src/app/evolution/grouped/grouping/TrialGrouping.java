package app.evolution.grouped.grouping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.evolution.grouped.JasimaGroupedIndividual;
import app.evolution.grouped.IJasimaGrouping;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

// TODO need to fix.
public class TrialGrouping implements IJasimaGrouping {

	private static final long serialVersionUID = -6134155663644563813L;

	private Map<GPIndividual, JasimaGroupedIndividual[]> indGroupMap = new HashMap<GPIndividual, JasimaGroupedIndividual[]>();

	public static final String P_GROUP_SIZE = "group_size";
	public static final String P_NUM_TRIALS = "num_trials";

	public static final int DEFAULT_GROUP_SIZE = 3;
	public static final int DEFAULT_NUM_TRIALS = 3;

	private int groupSize = DEFAULT_GROUP_SIZE;
	private int numTrials = DEFAULT_NUM_TRIALS;

	private JasimaGroupedIndividual bestEnsemble = null;
	private KozaFitness bestEnsembleFitness = new KozaFitness();

	private JasimaGroupedIndividual bestEnsembleOfGeneration = null;
	private KozaFitness bestEnsembleOfGenerationFitness = new KozaFitness();

	@Override
	public void setup(EvolutionState state, Parameter base) {
		try {
			groupSize = state.parameters.getInt(base.push(P_GROUP_SIZE), null);
			numTrials = state.parameters.getInt(base.push(P_NUM_TRIALS), null);

			bestEnsembleFitness.setStandardizedFitness(state, Double.MAX_VALUE);
			bestEnsembleOfGenerationFitness.setStandardizedFitness(state, Double.MAX_VALUE);
		} catch (NumberFormatException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	@Override
	public void groupIndividuals(EvolutionState state, int threadnum) {
		Subpopulation population = state.population.subpops[0];

		for (int i = 0; i < population.individuals.length; i++) {
			GPIndividual ind = (GPIndividual) population.individuals[i];
			if (indGroupMap.containsKey(ind)) {
				continue;
			}

			indGroupMap.put(ind, getGrouping(state, population, ind));
		}
	}

	private JasimaGroupedIndividual[] getGrouping(EvolutionState state,
			Subpopulation population,
			GPIndividual ind) {
		JasimaGroupedIndividual[] grouping = new JasimaGroupedIndividual[numTrials];

		Set<Individual> indSet = new HashSet<Individual>();
		indSet.add(ind);

		for (int i = 0; i < numTrials; i++) {
			GPIndividual[] inds = new GPIndividual[groupSize];
			for (int j = 0; j < groupSize; j++) {
				inds[j] = getIndWithoutReplacement(state, population, ind, indSet);
			}

			grouping[i] = new JasimaGroupedIndividual(inds);
		}

		return grouping;
	}

	private GPIndividual getIndWithoutReplacement(EvolutionState state,
			Subpopulation population,
			GPIndividual ind,
			Set<Individual> indSet) {
		// TODO make sure to test this.
		while (true) {
			int index = state.random[0].nextInt(population.individuals.length);
			GPIndividual sample = (GPIndividual) population.individuals[index];

			if (!indSet.contains(sample)) {
				indSet.add(sample);
				return sample;
			}
		}
	}

	@Override
	public int getGroupSize() {
		return groupSize;
	}

	@Override
	public int getNumTrials() {
		return numTrials;
	}

	@Override
	public boolean isIndEvaluated() {
		return true;
	}

	@Override
	public boolean isGroupEvaluated() {
		return false;
	}

	@Override
	public JasimaGroupedIndividual[] getGroups(Individual ind) {
		return indGroupMap.get(ind);
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
