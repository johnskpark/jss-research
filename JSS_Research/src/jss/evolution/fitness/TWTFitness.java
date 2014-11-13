package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.problem.Statistics;
import ec.Individual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class TWTFitness implements IFitness {

	/**
	 * TODO javadoc.
	 */
	public TWTFitness() {
	}

	@Override
	public double getFitness(Statistics stats, Individual ind) {
		return stats.getAverageTWT();
	}

}
