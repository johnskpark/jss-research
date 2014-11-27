package jss.evolution.fitness;

import jss.evolution.ISimpleFitness;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MakespanFitness implements ISimpleFitness {

	/**
	 * TODO javadoc.
	 */
	public MakespanFitness() {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageMakespan();
	}

}
