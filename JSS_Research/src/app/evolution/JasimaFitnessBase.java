package app.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

/**
 * The abstract base used for the fitness class that assigns the fitness to the GP individuals.
 *
 * @author parkjohn
 *
 */
public abstract class JasimaFitnessBase<T extends JasimaReproducible> implements IJasimaFitness<T> {

	private List<Double> instanceFitnesses = new ArrayList<Double>();
	private double sumFitness = 0.0;

	private JasimaGPProblem problem;

	protected List<Double> getInstanceFitnesses() {
		return instanceFitnesses;
	}

	protected double getSumFitness() {
		return sumFitness;
	}

	protected JasimaGPProblem getProblem() {
		return problem;
	}

	@Override
	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;
	}

	@Override
	public void accumulateFitness(final int index,
			final SimConfig config,
			final T reproducible,
			final Map<String, Object> results) {
		double fitness = getFitness(index, config, reproducible, results);

		instanceFitnesses.add(fitness);
		sumFitness += fitness;
	}

	@Override
	public void setFitness(final EvolutionState state,
			final SimConfig config,
			final T reproducible) {
		double finalFitness = getFinalFitness(state, config, reproducible);

		((KozaFitness) reproducible.getFitness()).setStandardizedFitness(state, finalFitness);
	}

	@Override
	public double getFinalFitness(final EvolutionState state,
			final SimConfig config,
			final T reproducible) {
		double avgFitness = sumFitness / instanceFitnesses.size();

		return avgFitness;
	}

	@Override
	public void clear() {
		instanceFitnesses.clear();
		sumFitness = 0.0;
	}

}
