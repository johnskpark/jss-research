package app.evaluation.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.ITrackedRule;
import app.TrackedRuleBase;
import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.IJasimaEvalFitness;
import app.evaluation.JasimaEvalProblem;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PrioRuleTarget;

public class RuleRankFitness implements IJasimaEvalFitness {

	private JasimaEvalProblem problem;

	@Override
	public String getHeaderName() {
		return "RuleRankDecisions";
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
		this.problem = problem;
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
		// Check to make sure that the rule is multirule
		if (!(rule instanceof ITrackedRule)) {
			throw new RuntimeException("The rule being evaluated must be a type of tracked rule.");
		}

		List<EvalPriorityRuleBase> referenceRules = problem.getReferenceRules();

		List<JasimaExperiment<INode>> trackedResults = tracker.getResults();
		JasimaExperiment<INode> trackedResult = trackedResults.get(configIndex);
		List<JasimaDecision<INode>> decisions = trackedResult.getDecisions();

		List<List<Integer>> ruleRankVectors = new ArrayList<>();
		for (int i = 0; i < referenceRules.size(); i++) {
			ruleRankVectors.add(new ArrayList<>());
		}

		for (int i = 0; i < decisions.size(); i++) {
			JasimaDecision<INode> decision = decisions.get(i);
			List<PrioRuleTarget> jobRankings = decision.getEntryRankings(rule);

			for (int j = 0; j < referenceRules.size(); j++) {
				PrioRuleTarget selectedJob = decision.getSelectedEntry(referenceRules.get(j));

				for (int k = 0; k < jobRankings.size(); k++) {
					if (jobRankings.get(k).equals(selectedJob)) {
						ruleRankVectors.get(j).add(k);
					}
				}
			}
		}

		String output = String.format("{%s}", formatRuleRanks(ruleRankVectors, referenceRules));

		return output;
	}

	protected String formatRuleRanks(List<List<Integer>> ruleRankVectors, List<EvalPriorityRuleBase> referenceRules) {
		List<String> ruleRankStrs = new ArrayList<>(referenceRules.size());

		for (int i = 0; i < referenceRules.size(); i++) {
			EvalPriorityRuleBase referenceRule = referenceRules.get(i);
			List<Integer> ruleRankVector = ruleRankVectors.get(i);

			ruleRankStrs.add(String.format("(%s:%s)",
					referenceRule.getClass().getSimpleName(),
					ruleRankVector.stream().map(x -> x+"").collect(Collectors.joining(","))));
		}

		return ruleRankStrs.stream().collect(Collectors.joining(","));
	}

}
