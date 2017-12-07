package app.evolution.grouped;

import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public abstract class JasimaGroupFitness implements IJasimaFitness<JasimaGPIndividual> {

	private JasimaGPProblem problem;

	public JasimaGPProblem getProblem() {
		return problem;
	}

	@Override
	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;
	}

	@Override
	public void accumulateFitness(final int index,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results) {
		// TODO
	}

	public abstract void accumulateIndFitness(final int index,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results);

	public abstract void accumulateGroupFitness(final int index,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results);

	@Override
	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind) {
		setIndFitness(state, config, ind);
		setGroupFitness(state, config, ind, new JasimaGroupedIndividual(new GPIndividual[]{(GPIndividual) ind}));
	}

	public abstract void setIndFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind);

	public abstract void setGroupFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final JasimaGroupedIndividual group);

	@Override
	public void clear() {
		clearIndFitness();
		clearGroupFitness();
	}

	public abstract void clearIndFitness();

	public abstract void clearGroupFitness();

}
