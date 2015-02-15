package app.evaluation.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;

public class EnsemblePriorityRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 543262729792177270L;

	public static final double ATC_K_VALUE = 3.0;

	private List<INode> rules;
	private int ruleNum;

	private Map<PrioRuleTarget, Integer> jobVotes = new HashMap<PrioRuleTarget, Integer>();

	public EnsemblePriorityRule() {
		super();

		setTieBreaker(new ATC(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		this.rules = config.getRules();
		this.ruleNum = config.getRuleNum();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		jobVotes.clear();

		for (int i = 0; i < q.size(); i++) {
			jobVotes.put(q.get(i), 0);
		}

		int[] decisions = new int[ruleNum];

		// TODO make this more efficient later down the line, but first get the functionality working.
		for (int i = 0; i < ruleNum; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				JasimaEvalData data = new JasimaEvalData();
				data.setPrioRuleTarget(entry);

				double priority = rules.get(i).evaluate(data);
				if (priority > bestPriority) {
					bestPriority = priority;
					bestIndex = j;
				}
			}

			// Increment the vote on the particular job.
			PrioRuleTarget bestEntry = q.get(bestIndex);
			jobVotes.put(bestEntry, jobVotes.get(bestEntry)+1);

			// Store the decisions.
			decisions[i] = bestIndex;
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry);
	}

}
