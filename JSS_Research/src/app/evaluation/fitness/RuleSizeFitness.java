package app.evaluation.fitness;

import app.TrackedRuleBase;
import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.IJasimaEvalFitness;
import app.evaluation.JasimaEvalProblem;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;

public class RuleSizeFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "NumRules,NumNodes";
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
		EvalPriorityRuleBase evalRule = (EvalPriorityRuleBase) rule;

		int numRules = evalRule.getNumRules();
		String output = numRules + ",\"";

		for (int i = 0; i < numRules; i++) {
			if (i != 0) {
				output += ",";
			}

			output += evalRule.getRuleSize(i);
		}

		output += "\"";
		return output;
	}

}
