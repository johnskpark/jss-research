package app.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public abstract class AbsJasimaFitness<T extends JasimaReproducible> implements IJasimaFitness<T> {

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
	public void accumulateFitness(final int index, final T reproducible, final Map<String, Object> results) {
		double fitness = getFitness(index, reproducible, results);

		instanceFitnesses.add(fitness);
		sumFitness += fitness;
	}

	protected abstract double getFitness(final int index, final T reproducible, final Map<String, Object> results);

	@Override
	public void setFitness(final EvolutionState state, final T reproducible) {
		double finalFitness = getFinalFitness(state, reproducible);

		((KozaFitness) reproducible.getFitness()).setStandardizedFitness(state, finalFitness);
	}

	public double getFinalFitness(final EvolutionState state, final T reproducible) {
		double avgFitness = sumFitness / instanceFitnesses.size();

		return avgFitness;
	}

	@Override
	public void clear() {
		instanceFitnesses.clear();
		sumFitness = 0.0;
	}

}
