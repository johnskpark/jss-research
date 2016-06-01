package app.evaluation.fitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.IMultiRule;
import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.JasimaPriorityStat;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class DiversityFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		// The diversity measures for a single problem instance are:
		// - The number of times all the rules vote for the same job.
		// - The number of times the tie-breaker is used (i.e. the same number of votes on two jobs).
		// - For each rule:
		// * The number of times they partook in the majority
		// * The number of times they partook in the minority

		String allSingleJobNum = "SingleJobNum";
		String tieBreakNum = "TieBreakNum";
		String ruleMajorityNum = "RuleMajorityNum";
		String ruleMinorityNum = "RuleMinorityNum";

		return String.format("%s,%s,%s,%s",
				allSingleJobNum,
				tieBreakNum,
				ruleMajorityNum,
				ruleMinorityNum);
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

		@SuppressWarnings("unchecked")
		IMultiRule<INode> solver = (IMultiRule<INode>) rule;
		List<JasimaExperiment<INode>> experiments = tracker.getResults();

		String[] experimentResults = new String[5];
		for (JasimaExperiment<INode> experiment : experiments) {
			experimentResults[0] = getSingleVotedJobResults(solver, results, experiment);
			experimentResults[1] = getTieBreakJobResults(solver, results, experiment);
			experimentResults[2] = getMajorityResults(solver, results, experiment);
			experimentResults[3] = getMinorityResults(solver, results, experiment);
		}

		return String.format("%s,%s,%s,%s", experimentResults[0],
				experimentResults[1],
				experimentResults[2],
				experimentResults[3]);
	}

	protected String getSingleVotedJobResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		double singleVoteJobCount = 0.0;
		for (JasimaDecision<INode> decision : decisions) {
			PrioRuleTarget selectedJob = decision.getSelectedEntry(solver);
			JasimaPriorityStat[] stats = decision.getStats(solver);

			boolean singleVotedJob = true;
			for (int i = 0; i < stats.length && singleVotedJob; i++) {
				if (!selectedJob.equals(stats[i].getBestEntry())) {
					singleVotedJob = false;
				}
			}

			if (singleVotedJob) {
				singleVoteJobCount++;
			}
		}

		return String.format("%f", singleVoteJobCount / decisions.size());
	}

	protected String getTieBreakJobResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();

		double tieBreakJobCount = 0.0;
		for (JasimaDecision<INode> decision : decisions) {
			JasimaPriorityStat[] stats = decision.getStats(solver);

			Map<PrioRuleTarget, Integer> jobScores = new HashMap<>();
			for (PrioRuleTarget entry : decision.getEntries()) {
				jobScores.put(entry, 0);
			}

			for (int i = 0; i < stats.length; i++) {
				PrioRuleTarget bestEntry = stats[i].getBestEntry();
				jobScores.put(bestEntry, jobScores.get(bestEntry) + 1);
			}

			List<Integer> scores = new ArrayList<>(jobScores.values());
			Collections.sort(scores);

			if (scores.size() >= 2 && scores.get(scores.size() - 1) == scores.get(scores.size() - 2)) {
				tieBreakJobCount++;
			}
		}

		return String.format("%f", tieBreakJobCount / decisions.size());
	}

	protected String getMajorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();
		List<INode> ruleComponents = solver.getRuleComponents();

		double[] majorityCounts = new double[ruleComponents.size()];

		for (JasimaDecision<INode> decision: decisions) {
			PrioRuleTarget selectedJob = decision.getSelectedEntry(solver);
			JasimaPriorityStat[] stats = decision.getStats(solver);

			for (int i = 0; i < stats.length; i++) {
				if (stats[i].getBestEntry().equals(selectedJob)) {
					majorityCounts[i]++;
				}
			}
		}

		String majorityResults = String.format("\"%f", 1.0 * majorityCounts[0] / decisions.size());
		for (int i = 1; i < ruleComponents.size(); i++) {
			majorityResults += String.format(",%f", 1.0 * majorityCounts[i] / decisions.size());
		}
		majorityResults += "\"";

		return majorityResults;
	}

	protected String getMinorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> experiment) {
		List<JasimaDecision<INode>> decisions = experiment.getDecisions();
		List<INode> ruleComponents = solver.getRuleComponents();

		double[] minorityCounts = new double[ruleComponents.size()];

		for (JasimaDecision<INode> decision: decisions) {
			List<PrioRuleTarget> jobRankings = decision.getEntryRankings(solver);
			PrioRuleTarget worstJob = jobRankings.get(jobRankings.size() - 1);
			JasimaPriorityStat[] stats = decision.getStats(solver);

			for (int i = 0; i < stats.length; i++) {
				if (stats[i].getBestEntry().equals(worstJob)) {
					minorityCounts[i]++;
				}
			}
		}

		String minorityResults = String.format("%f", 1.0 * minorityCounts[0] / decisions.size());
		for (int i = 1; i < ruleComponents.size(); i++) {
			minorityResults += String.format(",%f", 1.0 * minorityCounts[i] / decisions.size());
		}

		return minorityResults;
	}

}
