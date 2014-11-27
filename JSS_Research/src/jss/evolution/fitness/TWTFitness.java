package jss.evolution.fitness;

import jss.evolution.ISimpleFitness;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class TWTFitness implements ISimpleFitness {

	/**
	 * TODO javadoc.
	 */
	public TWTFitness() {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageTWT();
	}

}
