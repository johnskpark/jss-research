package app.evolution.coop;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPProblem;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.Individual;

public abstract class JasimaCoopFitness implements IJasimaFitness<JasimaCoopIndividual> {

	private static final boolean DEFAULT_SHOULD_SET_CONTEXT = false;

	private JasimaGPProblem problem;

	private Individual[] individuals;
	private IJasimaFitness<JasimaCoopIndividual>[] individualFitnesses;
	private boolean[] updateFitness;
	private int numInds;

	private boolean shouldSetContext = DEFAULT_SHOULD_SET_CONTEXT;

	@SuppressWarnings("unchecked")
	public void loadIndividuals(final Individual[] inds) {
		if (numInds != inds.length) {
			numInds = inds.length;

			individuals = new Individual[numInds];
			individualFitnesses = new IJasimaFitness[numInds];
			updateFitness = new boolean[numInds];
		}

		for (int i = 0; i < inds.length; i++) {
			individuals[i] = inds[i];
			individualFitnesses[i] = generateFitness(inds);
			individualFitnesses[i].setProblem(getProblem());
		}
	}

	protected abstract IJasimaFitness<JasimaCoopIndividual> generateFitness(final Individual[] inds);

	public JasimaGPProblem getProblem() {
		return problem;
	}

	protected Individual[] getIndividuals() {
		return individuals;
	}

	protected boolean shouldSetContext() {
		return shouldSetContext;
	}

	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;
	}

	@Override
	@Deprecated
	public void accumulateFitness(final int expIndex,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			final Map<String, Object> results) {
		int index = getFitnessIndex(ind);
		if (index == -1) {
			throw new RuntimeException("Individual " + ind + " has not been loaded into the fitness.");
		}

		accumulateFitness(expIndex, config, ind, results, index);
	}

	public void accumulateFitness(final int expIndex,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			final Map<String, Object> results,
			int index) {
		IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses[index];

		fitness.accumulateFitness(expIndex, config, ind, results);
	}

	@Override
	@Deprecated
	public double getFitness(final int expIndex,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			final Map<String, Object> results) {
		int index = getFitnessIndex(ind);
		if (index == -1) {
			throw new RuntimeException("Individual " + ind + " has not been loaded into the fitness.");
		}

		return getFitness(expIndex, config, ind, results, index);
	}

	public double getFitness(final int expIndex,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			final Map<String, Object> results, int index) {
		IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses[index];

		return fitness.getFitness(expIndex, config, ind, results);
	}

	// Set the update fitness somewhere here.

	public void setUpdateConfiguration(final Individual[] inds, final boolean[] updateFitness, final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			this.updateFitness[i] = updateFitness[i];
		}

		this.shouldSetContext = shouldSetContext;
	}

	protected boolean[] updateFitness() {
		return updateFitness;
	}

	@Override
	@Deprecated
	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaCoopIndividual ind) {
		int index = getFitnessIndex(ind);
		if (index == -1) {
			throw new RuntimeException("Individual " + ind + " has not been loaded into the fitness.");
		}

		setFitness(state, config, ind, index);
	}

	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			int index) {
		if (updateFitness[index]) {
			ind.getFitness().setContext(individuals, index);

			IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses[index];

			fitness.setFitness(state, config, ind);
		}
	}

	@Override
	@Deprecated
	public double getFinalFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaCoopIndividual ind) {
		int index = getFitnessIndex(ind);
		if (index == -1) {
			throw new RuntimeException("Individual " + ind + " has not been loaded into the fitness.");
		}
		return getFinalFitness(state, config, ind, index);
	}

	public double getFinalFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaCoopIndividual ind,
			int index) {
		IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses[index];

		return fitness.getFinalFitness(state, config, ind);
	}

	private int getFitnessIndex(JasimaCoopIndividual ind) {
		for (int i = 0; i < individuals.length; i++) {
			if (individuals[i].equals(ind)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void clear() {
		for (int i = 0; i < numInds; i++) {
			individuals[i] = null;
			individualFitnesses[i] = null;
			updateFitness[i] = false;
		}

		shouldSetContext = DEFAULT_SHOULD_SET_CONTEXT;
	}

}
