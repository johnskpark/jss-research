package app.stat;

import java.util.Map;

import jasima.core.statistics.SummaryStat;

public class TardinessStat {

	private static final String T_MEAN_STR = "tardiness";

	public static double getTotalTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(T_MEAN_STR);

		return stat.sum();
	}

	public static double getMeanTardiness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(T_MEAN_STR);

		return stat.mean();
	}

	public static double getNormTotalTardiness(final Map<String, Object> results, final double factor) {
		double twt = getTotalTardiness(results);

		return twt / factor;
	}

	public static double getNormMeanTardiness(final Map<String, Object> results, final double factor) {
		double mwt = getMeanTardiness(results);

		return mwt / factor;
	}

}
