package jss.evolution.fitness;

import jss.evolution.IFitness;
import jss.evolution.JSSGPGroupedProblem;
import jss.evolution.statistic_data.PenaltyData;
import jss.problem.Statistics;
import ec.Individual;

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
	public double getFitness(Statistics stats, Individual ind) {
		PenaltyData penaltyData = (PenaltyData) stats.getData(JSSGPGroupedProblem.TRACKER_DATA);

		return stats.getAverageMakespanDeviation() * (1 + penaltyData.getAveragePenalty(ind));
	}

}
