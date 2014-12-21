package jss.evolution.fitness;

import jss.evolution.IGroupedFitness;
import jss.evolution.JSSGPCoopProblem;
import jss.evolution.statistic_data.PenaltyData;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleFitness implements IGroupedFitness {

	/**
	 * TODO javadoc.
	 */
	public CoopEnsembleFitness() {
	}

	@Override
	public double getFitness(Statistics stats, int index) {
		PenaltyData penaltyData = (PenaltyData) stats.getData(JSSGPCoopProblem.TRACKER_DATA);

		return stats.getAverageMakespanDeviation() * (1 + penaltyData.getAveragePenalty(index));
	}

}
