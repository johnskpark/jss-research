package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

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
	public double getNumericResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
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
