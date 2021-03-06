package app.evaluation.fitness;

import app.TrackedRuleBase;
import app.evaluation.IJasimaEvalFitness;
import app.evaluation.JasimaEvalProblem;
import app.node.INode;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;

public class MaxWeightedTardinessFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "MaxWeightedTardiness";
	}

	@Override
	public boolean resultIsNumeric() {
		return true;
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
		return WeightedTardinessStat.getMaxWeightedTardiness(experiment.getResults());
	}

	@Override
	public String getStringResult(final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		return String.format("%f", WeightedTardinessStat.getMaxWeightedTardiness(experiment.getResults()));
	}
}
