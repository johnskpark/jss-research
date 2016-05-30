package app.evaluation.fitness;

import java.util.List;
import java.util.Map;

import app.IMultiRule;
import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.tracker.JasimaDecision;
import app.tracker.JasimaDecisionMaker;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.JasimaPriorityStat;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

// TODO Right, what do I need to measure again?
public class DiversityFitness implements IJasimaEvalFitness {

	// TODO need to define the distance measures here.

	@Override
	public String getHeaderName() {
		// The diversity measures for a single problem instance are:
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
		// Check to make sure that the rule is multirule
		if (!(rule instanceof IMultiRule)) {
			throw new RuntimeException("The rule being evaluated must be a type of multirule.");
		}

		IMultiRule<INode> solver = (IMultiRule<INode>) rule;
		List<JasimaExperiment<INode>> experiments = tracker.getResults();

		String[] experimentResults = new String[5];
		for (JasimaExperiment<INode> experiment : experiments) {
			experimentResults[0] = getSingleVotedJobResults(results, experiment);
			experimentResults[1] = getTieBreakJobResults(results, experiment);
			experimentResults[2] = getMajorityResults(solver, results, experiment);
			experimentResults[3] = getMinorityResults(solver, results, experiment);
			experimentResults[4] = getPriorityResults(solver, results, experiment);
		}

		return String.format("%s,%s,%s,%s,%s", experimentResults[0],
				experimentResults[1],
				experimentResults[2],
				experimentResults[3],
				experimentResults[4]);
	}

	protected String getSingleVotedJobResults(Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		double singleVoteJobCount = 0.0;
		for (JasimaDecision<INode> decision : decisions) {
			PrioRuleTarget selectedJob = decision.getSelectedEntry();

			for (JasimaDecisionMaker decisionMaker : experiment.getDecisionMakers()) {
				// TODO
			}
		}

		return String.format("%f", singleVoteJobCount / decisions.size());
	}

	protected String getTieBreakJobResults(Map<String, Object> results, JasimaExperiment<INode> experiment) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO this part shouldn't be a string. Now it is.
	protected String getMajorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		for (JasimaDecision<INode> decision: decisions) {
			JasimaPriorityStat[] stats = decision.getStats(solver);

			// TODO
		}

		return null;
	}

	// TODO this part shouldn't be a string. Now it is.
	protected String getMinorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		for (JasimaDecision<INode> decision: decisions) {
			JasimaPriorityStat[] stats = decision.getStats(solver);

			// TODO
		}

		return null;
	}

	// TODO this part shouldn't be a string. Now it is.
	protected String getPriorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		for (JasimaDecision<INode> decision: decisions) {
			JasimaPriorityStat[] stats = decision.getStats(solver);

			// TODO
		}

		return null;
	}
}
