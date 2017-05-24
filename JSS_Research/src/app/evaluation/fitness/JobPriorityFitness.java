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

public class JobPriorityFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		// Get the list of all the indices of the jobs that are selected.
		// Get the list of all the priorities assigned to the waiting jobs.

		String jobSelected = "JobSelected";
		String jobPriorities = "JobPriorities";

		return String.format("%s,%s",
				jobSelected,
				jobPriorities);
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
				getJobSelectedResults(solver, results, trackedResult),
				getJobPriorityResults(solver, results, trackedResult)
		};


		String output = String.format("%s", experimentResults[0]);
		for (int i = 1; i < experimentResults.length; i++) {
			output += String.format(",%s", experimentResults[i]);
		}

		return output;
	}

	protected String getJobSelectedResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();
		StringBuilder jobSelected = new StringBuilder();

		jobSelected.append("\"");
		for (int i = 0; i < decisions.size(); i++) {
			JasimaDecision<INode> decision = decisions.get(i);

			if (i != 0) { jobSelected.append(","); }
			jobSelected.append(decision.getSelectedEntry(solver).getJobNum());
		}
		jobSelected.append("\"");

		return jobSelected.toString();
	}

	protected String getJobPriorityResults(IMultiRule<INode> solver, Map<String, Object> results, JasimaExperiment<INode> trackedResult) {
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();
		StringBuilder jobPriority = new StringBuilder();

		jobPriority.append("\"");
		for (int i = 0; i < decisions.size(); i++) {
			JasimaDecision<INode> decision = decisions.get(i);

			if (i != 0) { jobPriority.append(","); }
			jobPriority.append("\'");

			JasimaPriorityStat stat = decision.getStats(solver)[0];
			for (int j = 0; j < stat.getEntries().length; j++) {
				if (j != 0) { jobPriority.append(","); }

				jobPriority.append(String.format("(%d:%.4f)",
						stat.getEntries()[j].getJobNum(),
						stat.getPriorities()[j]));
			}
			jobPriority.append("\'");
		}
		jobPriority.append("\"");

		return jobPriority.toString();
	}


	// TODO old writing, remove after implementation.
	// TODO double check this.
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
