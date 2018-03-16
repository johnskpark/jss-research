package app.evaluation;

import app.TrackedRuleBase;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;

public interface IJasimaEvalFitness {

	public String getHeaderName();

	public boolean resultIsNumeric();

	public void beforeExperiment(final JasimaEvalProblem problem,
			final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker);

	public double getNumericResult(final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker);

	public String getStringResult(final TrackedRuleBase<INode> rule,
			final SimConfig simConfig,
			final int configIndex,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker);

}
