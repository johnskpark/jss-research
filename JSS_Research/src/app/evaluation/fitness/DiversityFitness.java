package app.evaluation.fitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.IMultiRule;
import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.JasimaPriorityStat;
import jasima.shopSim.core.JobShopExperiment;
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
		String ruleRankNum = "RuleRankNum";

		return String.format("%s,%s,%s,%s,%s",
				allSingleJobNum,
				tieBreakNum,
				ruleMajorityNum,
				ruleMinorityNum,
				ruleRankNum);
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
		// Check to make sure that the rule is multirule
		if (!(rule instanceof IMultiRule)) {
			throw new RuntimeException("The rule being evaluated must be a type of multirule.");
		}

		Map<String, Object> results = experiment.getResults();

		@SuppressWarnings("unchecked")
		IMultiRule<INode> solver = (IMultiRule<INode>) rule;
		List<JasimaExperiment<INode>> trackedResults = tracker.getResults();

		JasimaExperiment<INode> trackedResult = trackedResults.get(configIndex);

		String[] experimentResults = new String[] {
				getSingleVotedJobResults(solver, results, trackedResult),
				getTieBreakJobResults(solver, results, trackedResult),
				getMajorityResults(solver, results, trackedResult),
				getMinorityResults(solver, results, trackedResult),
				getRankResults(solver, results, trackedResult)
		};


		String output = String.format("%s", experimentResults[0]);
		for (int i = 1; i < experimentResults.length; i++) {
			output += String.format(",%s", experimentResults[i]);
		}

		return output;
	}

	protected String getSingleVotedJobResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();

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

	protected String getTieBreakJobResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();

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

	protected String getMajorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();
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

	protected String getMinorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();
		List<INode> ruleComponents = solver.getRuleComponents();

		double[] minorityCounts = new double[ruleComponents.size()];

		for (JasimaDecision<INode> decision: decisions) {
			List<PrioRuleTarget> jobRankings = decision.getEntryRankings(solver);
			JasimaPriorityStat[] stats = decision.getStats(solver);

			boolean foundHit = false;
			for (int i = jobRankings.size() - 1; i >= 0 && !foundHit; i--) {
				PrioRuleTarget job = jobRankings.get(i);

				for (int j = 0; j < stats.length; j++) {
					if (stats[j].getBestEntry().equals(job)) {
						minorityCounts[j]++;
						foundHit = true;
					}
				}
			}
		}

		String minorityResults = String.format("\"%f", 1.0 * minorityCounts[0] / decisions.size());
		for (int i = 1; i < ruleComponents.size(); i++) {
			minorityResults += String.format(",%f", 1.0 * minorityCounts[i] / decisions.size());
		}
		minorityResults += "\"";

		return minorityResults;
	}

	protected String getRankResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();
		List<INode> ruleComponents = solver.getRuleComponents();

		double[] ranks = new double[ruleComponents.size()];

		for (JasimaDecision<INode> decision : decisions) {
			List<PrioRuleTarget> jobRankings = decision.getEntryRankings(solver);
			JasimaPriorityStat[] stats = decision.getStats(solver);

			for (int i = 0; i < stats.length; i++) {
				int rank = jobRankings.indexOf(stats[i].getBestEntry());
				ranks[i] += 1.0 * rank / jobRankings.size();
			}
		}

		String lowRankResults = String.format("\"%f", 1.0 * ranks[0] / decisions.size());
		for (int i = 1; i < ruleComponents.size(); i++) {
			lowRankResults += String.format(",%f", 1.0 * ranks[i] / decisions.size());
		}
		lowRankResults += "\"";

		return lowRankResults;
	}

}
