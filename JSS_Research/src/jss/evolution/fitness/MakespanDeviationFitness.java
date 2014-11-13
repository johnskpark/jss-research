package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.problem.Statistics;
import ec.Individual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MakespanDeviationFitness implements IFitness {

	/**
	 * TODO javadoc.
	 */
	public MakespanDeviationFitness() {
	}

	@Override
	public double getFitness(Statistics stats, Individual ind) {
		return stats.getAverageMakespanDeviation();
	}
}
