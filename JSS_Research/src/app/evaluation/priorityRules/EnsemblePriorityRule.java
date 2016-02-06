package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.priorityRules.ATCPR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class EnsemblePriorityRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 543262729792177270L;

	public static final double ATC_K_VALUE = 3.0;

	private List<INode> rules;
	private int ruleNum;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRankings = new ArrayList<EntryVotes>();

	public EnsemblePriorityRule() {
		super();
		setTieBreaker(new ATCPR(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		setSeed(config.getSeed());
		setNodeData(config.getNodeData());

		this.rules = config.getRules();
		this.ruleNum = config.getRules().size();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Clear and repopulate the job votes.
		clear();

		for (int i = 0; i < q.size(); i++) {
			EntryVotes ev = new EntryVotes(q.get(i));
			jobVotes.put(q.get(i), ev);
			jobRankings.add(ev);
		}

		// Initialise the decisions vector.
		int[] decisions = new int[ruleNum];

		// Go through the individuals and vote on the decisions.
		for (int i = 0; i < ruleNum; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				getNodeData().setEntry(q.get(j));

				double priority = rules.get(i).evaluate(getNodeData());
				if (priority > bestPriority) {
					bestPriority = priority;
					bestIndex = j;
				}
			}

			// Increment the vote on the particular job.
			PrioRuleTarget bestEntry = q.get(bestIndex);
			jobVotes.get(bestEntry).increment();

			// Store the decisions.
			decisions[i] = bestIndex;
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry).getCount();
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName() + "[ " + rules.get(0).toString());

		for (int i = 1; i < ruleNum; i++) {
			builder.append("," + rules.get(i).toString());
		}

		builder.append(" ]");
		return builder.toString();
	}

	public void clear() {
		jobVotes.clear();
		jobRankings.clear();
	}

	private class EntryVotes implements Comparable<EntryVotes> {
		private PrioRuleTarget entry;
		private int count = 0;

		public EntryVotes(PrioRuleTarget e) {
			entry = e;
		}

		public void increment() {
			count++;
		}

		public PrioRuleTarget getEntry() {
			return entry;
		}

		public int getCount() {
			return count;
		}

		@Override
		public int compareTo(EntryVotes other) {
			int diff = other.count - this.count;
			if (diff != 0) {
				return diff;
			} else {
				double prio1 = getTieBreaker().calcPrio(this.entry);
				double prio2 = getTieBreaker().calcPrio(other.entry);

				if (prio1 > prio2) {
					return -1;
				} else if (prio1 < prio2) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

}
