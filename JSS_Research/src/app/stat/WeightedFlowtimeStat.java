package app.stat;

import java.util.Map;

import jasima.core.statistics.SummaryStat;

public class WeightedFlowtimeStat {

	private static final String WF_MEAN_STR = "flowtime";

	public static double getTotalWeightedFlowtime(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WF_MEAN_STR);

		return stat.sum();
	}

	public static double getMeanWeightedFlowtime(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WF_MEAN_STR);

		return stat.mean();
	}

	public static double getNormTotalWeightedTardiness(final Map<String, Object> results, final double factor) {
		double twt = getTotalWeightedFlowtime(results);

		return twt / factor;
	}

	public static double getNormMeanWeightedFlowtime(final Map<String, Object> results, final double factor) {
		double mwt = getMeanWeightedFlowtime(results);

		return mwt / factor;
	}

}
