package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.problem.Statistics;

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
	public double getFitness(Statistics stats) {
		return stats.getAverageMakespanDeviation();
	}
}
