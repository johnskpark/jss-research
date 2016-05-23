package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.stat.WeightedTardinessStat;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class TWTFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "TWT";
	}

	@Override
	public boolean resultIsNumeric() {
		return true;
	}

	@Override
	public double getNumericResult(final PR rule, final Map<String, Object> results, JasimaExperimentTracker tracker) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
	}

	@Override
	public String getStringResult(final PR rule, final Map<String, Object> results, JasimaExperimentTracker tracker) {
		return String.format("%f", WeightedTardinessStat.getTotalWeightedTardiness(results));
	}

}
