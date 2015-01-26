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

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats) {
		double twt = stats.getAverageTWT();
		double penalty = ((PenaltyData) stats.getData(JSSGPGroupedProblem.TRACKER_DATA)).getAveragePenalty(0);

		System.out.printf("%f %f\n", twt, penalty);

		return twt + 0.5 * penalty; // TODO magic number.
	}

}
