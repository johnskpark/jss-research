package jss.evolution.fitness;

import java.util.List;

import jss.IProblemInstance;
import jss.evolution.ISimpleFitness;
import jss.evolution.JSSGPGroupedProblem;
import jss.evolution.statistic_data.PenaltyData;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopTWTFitness implements ISimpleFitness {

	public static final double LEARNING_RATIO = 0.5;

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats) {
		double twt = stats.getAverageTWT();
		double penalty = ((PenaltyData) stats.getData(JSSGPGroupedProblem.TRACKER_DATA)).getAveragePenalty(0);

		return twt * (1.0 + LEARNING_RATIO * penalty);
	}

}
