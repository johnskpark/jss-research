package app.evaluation.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;

public class TWTFitness implements IJasimaEvalFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	@Override
	public String getHeaderName() {
		return "TWT";
	}
	
	@Override
	public double getRelevantResult(Map<String, Object> results) {
		SummaryStat stat = (SummaryStat)results.get(WT_MEAN_STR);

		return stat.sum();
	}

}
