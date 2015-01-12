package jss.evolution.fitness;

import java.util.List;

import jss.IProblemInstance;
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
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats) {
		return stats.getAverageTWT();
	}

}
