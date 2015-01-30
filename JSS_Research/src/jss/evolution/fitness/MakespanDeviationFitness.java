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
public class MakespanDeviationFitness implements ISimpleFitness {

	private static final long serialVersionUID = 3228606491209119832L;

	/**
	 * TODO javadoc.
	 */
	public MakespanDeviationFitness() {
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageMakespanDeviation();
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats) {
		double fitness = getFitness(stats);

		((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
	}

}
