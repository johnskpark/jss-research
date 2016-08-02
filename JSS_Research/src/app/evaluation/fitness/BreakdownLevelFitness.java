package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

public class BreakdownLevelFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "BreakdownLevel";
	}

	@Override
	public boolean resultIsNumeric() {
		return true;
	}

	@Override
	public double getNumericResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		if (!(simConfig instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		return ((DynamicBreakdownSimConfig) simConfig).getBreakdownLevel(configIndex);
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		if (!(simConfig instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		return String.format("%f", ((DynamicBreakdownSimConfig) simConfig).getBreakdownLevel(configIndex));
	}


}
