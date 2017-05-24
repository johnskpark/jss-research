package app.evaluation.fitness;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.SimConfig;
import app.stat.WeightedFlowtimeStat;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
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
		return WeightedFlowtimeStat.getTotalWeightedFlowtime(experiment.getResults());
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		return String.format("%f", WeightedFlowtimeStat.getTotalWeightedFlowtime(experiment.getResults()));
	}

}
