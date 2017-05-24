package app.evaluation.fitness;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public class BreakdownLevelInfo implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "BreakdownLevel";
	}

	@Override
	public boolean resultIsNumeric() {
		return false;
	}

	@Override
	public void beforeExperiment(final PR rule,
			final SimConfig simConfig,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		// Do nothing.
	}

	@Override
	public double getNumericResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		if (!(simConfig instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		DynamicBreakdownSimConfig breakdownSimConfig = (DynamicBreakdownSimConfig) simConfig;

		return breakdownSimConfig.getBreakdownLevel(configIndex);
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		if (!(simConfig instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		DynamicBreakdownSimConfig breakdownSimConfig = (DynamicBreakdownSimConfig) simConfig;

		return String.format("%f", breakdownSimConfig.getBreakdownLevel(configIndex));
	}


}
