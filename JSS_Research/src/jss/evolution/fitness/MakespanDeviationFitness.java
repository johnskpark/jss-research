package jss.evolution.fitness;

import jss.evolution.ISimpleFitness;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MakespanDeviationFitness implements ISimpleFitness {

	/**
	 * TODO javadoc.
	 */
	public MakespanDeviationFitness() {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageMakespanDeviation();
	}
}
