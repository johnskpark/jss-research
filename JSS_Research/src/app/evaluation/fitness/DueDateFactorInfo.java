package app.evaluation.fitness;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.core.random.continuous.DblStream;
import jasima.core.util.Pair;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public class DueDateFactorInfo implements IJasimaEvalFitness {

	private static final double MINIMUM_THRESHOLD_FROM_ZERO = 0.000001;

	@Override
	public String getHeaderName() {
		return "DueDateFactor";
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
		if (!(simConfig instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		// FIXME This needs to be adjusted later down the line.
		DblStream ddf = ((DynamicBreakdownSimConfig) simConfig).getDueDateFactor(configIndex);
		Pair<Double, Double> ddfRange = ddf.getValueRange();

		if (Math.abs(ddfRange.a - ddfRange.b) > MINIMUM_THRESHOLD_FROM_ZERO) {
			return String.format("\"%f,%f\"", ddfRange.a, ddfRange.b);
		} else {
			return String.format("%f", ddfRange.a);
		}
	}


}
