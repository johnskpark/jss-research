package app.stat;

import java.util.Map;

import jasima.core.statistics.SummaryStat;

public class WeightedTardinessStat {

	private static final String WT_MEAN_STR = "weightedTardMean";

	public static double getTotalWeightedTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);
		
		return stat.sum();
	}
	
	public static double getMeanWeightedTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);
		
		return stat.mean();
	}
	
}
