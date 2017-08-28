package app.evaluation.fitness;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.DynamicSimConfig;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public class StopAfterNumJobsInfo implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "StopAfterNumJobs";
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
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		if (!((simConfig instanceof DynamicSimConfig) || (simConfig instanceof DynamicBreakdownSimConfig))) {
			throw new RuntimeException("SimConfig needs to be of DynamicSimConfig or DynamicBreakdownSimConfig instance.");
		}

		if (simConfig instanceof DynamicSimConfig) {
			return String.format("%d", ((DynamicSimConfig) simConfig).getStopAfterNumJobs(configIndex));
		} else if (simConfig instanceof DynamicBreakdownSimConfig) {
			return String.format("%d", ((DynamicBreakdownSimConfig) simConfig).getStopAfterNumJobs(configIndex));
		} else {
			return null; // Should not be reachable.
		}
	}


}