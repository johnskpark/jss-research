package app.evaluation.fitness;

import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.PR;

// TODO Right, what do I need to measure again?
public class DiversityFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		// The diversity measures are:
		// - The number of times all the rules vote for the same job.
		// - The number of times the tie-breaker is used (i.e. the same number of votes on two jobs).
		// - For each rule:
		// * The number of times they partook in the majority
		// * The number of times they partook in the minority
		// * The priorities assigned to each of the rules.

		String allSingleJobNum = "SingleJobNum";
		String tieBreakNum = "TieBreakNum";
		String ruleMajorityNum = "RuleMajorityNum";
		String ruleMinorityNum = "RuleMinorityNum";
		String rulePriorities = "RulePriorities";

		return String.format("%s,%s,%s,%s,%s",
				allSingleJobNum,
				tieBreakNum,
				ruleMajorityNum,
				ruleMinorityNum,
				rulePriorities);
	}

	@Override
	public boolean resultIsNumeric() {
		return false;
	}

	@Override
	public double getNumericResult(PR rule, Map<String, Object> results, JasimaExperimentTracker<INode> tracker) {
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(PR rule, Map<String, Object> results, JasimaExperimentTracker<INode> tracker) {
		// TODO Auto-generated method stub

		// TODO right, how does this tracker thing work again?

		return null;
	}

}
