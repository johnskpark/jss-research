package app.evaluation.fitness;

import org.apache.commons.math3.distribution.RealDistribution;

import app.TrackedRuleBase;
import app.evaluation.IJasimaEvalFitness;
import app.evaluation.JasimaEvalProblem;
import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;
import jasima.shopSim.core.JobShopExperiment;

public class RepairTimeDistributionInfo implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "RepairTimeFactor";
	}

	@Override
	public boolean resultIsNumeric() {
		return false;
	}

	@Override
	public void beforeExperiment(final JasimaEvalProblem problem,
			final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		// Do nothing.
	}

	@Override
	public double getNumericResult(final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		if (!(simConfig instanceof DynamicBreakdownSimConfig) || !(experiment instanceof DynamicBreakdownShopExperiment)) {
			throw new RuntimeException("SimConfig needs to be of DynamicBreakdownSimConfig instance.");
		}

		DynamicBreakdownSimConfig breakdownSimConfig = (DynamicBreakdownSimConfig) simConfig;
		DynamicBreakdownShopExperiment breakdownExperiment = (DynamicBreakdownShopExperiment) experiment;

		DblStream stream = breakdownSimConfig.getRepairTimeDistribution(breakdownExperiment, configIndex);
		if (stream instanceof DblDistribution) {
			RealDistribution distribution = ((DblDistribution) stream).getDistribution();
			return String.format("%s:%f", distribution.getClass().getSimpleName(), stream.getNumericalMean());
		} else {
			return String.format("%s:%f", stream.getClass().getSimpleName(), stream.getNumericalMean());
		}
	}


}
