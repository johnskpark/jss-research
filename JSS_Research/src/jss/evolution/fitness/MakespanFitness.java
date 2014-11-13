package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.problem.Statistics;
import ec.Individual;

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
	public double getFitness(Statistics stats, Individual ind) {
		return stats.getAverageMakespan();
	}

}
