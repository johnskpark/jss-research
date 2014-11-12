package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MakespanFitness implements IFitness {

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
