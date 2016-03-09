package app.stat;

import java.util.Map;

import jasima.core.statistics.SummaryStat;

public class WeightedTardinessStat {

	private static final String WT_MEAN_STR = "tardiness";

	public static double getTotalWeightedTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		return stat.sum();
	}

	public static double getMeanWeightedTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		return stat.mean();
	}

	public static double getNormTotalWeightedTardiness(final Map<String, Object> results, final double factor) {
		double twt = getTotalWeightedTardiness(results);

		return twt / factor;
	}

}
