package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.evolution.JSSGPGroupedProblem;
import jss.evolution.solvers.PriorityTracker;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleFitness implements IFitness {

	/**
	 * TODO javadoc.
	 */
	public CoopEnsembleFitness() {
	}

	@Override
	public double getFitness(Statistics stats) {
		PriorityTracker tracker = (PriorityTracker) stats.getData(JSSGPGroupedProblem.TRACKER_DATA);


		return stats.getAverageMakespanDeviation();
	}

}
