package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.IJasimaEvalFitness;
import app.stat.WeightedTardinessStat;

public class TWTFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "TWT";
	}

	@Override
	public String getRelevantResult(final AbsEvalPriorityRule solver, final Map<String, Object> results) {
		return String.format("%f", WeightedTardinessStat.getTotalWeightedTardiness(results));
	}

}
