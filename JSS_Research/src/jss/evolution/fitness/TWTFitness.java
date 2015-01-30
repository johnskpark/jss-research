package jss.evolution.fitness;

import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;
import jss.IProblemInstance;
import jss.evolution.ISimpleFitness;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class TWTFitness implements ISimpleFitness {

	private static final long serialVersionUID = -3598541545466196915L;

	/**
	 * TODO javadoc.
	 */
	public TWTFitness() {
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageTWT();
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats) {
		double fitness = getFitness(stats);

		((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
	}

}
