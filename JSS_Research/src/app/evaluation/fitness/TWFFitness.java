package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.stat.WeightedFlowtimeStat;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class TWFFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "TWF";
	}

	@Override
	public boolean resultIsNumeric() {
		return true;
	}

	@Override
	public double getNumericResult(final PR rule,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		return WeightedFlowtimeStat.getTotalWeightedFlowtime(results);
	}

	@Override
	public String getStringResult(final PR rule,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		return String.format("%f", WeightedFlowtimeStat.getTotalWeightedFlowtime(results));
	}

}
