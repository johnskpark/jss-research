package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.evaluation.EvalPriorityRuleBase;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class LinearCombinationRule extends EvalPriorityRuleBase {

	private static final long serialVersionUID = 543262729792177270L;

	public static final double ATC_K_VALUE = 3.0;

	private List<INode> rules;
	private int ruleNum;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRankings = new ArrayList<EntryVotes>();

	public LinearCombinationRule() {
		super();
		setTieBreaker(new ATC(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		setSeed(config.getSeed());
		setNodeData(config.getNodeData());

		this.rules = config.getRules();
		this.ruleNum = config.getRules().size();
	}

	@Override
	public List<INode> getRuleComponents() {
		return rules;
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

		// Go through the individuals and vote on the decisions.
		for (int i = 0; i < ruleNum; i++) {
			INode rule = rules.get(i);

			double[] priorities = new double[q.size()];
			double bestPriority = Double.NEGATIVE_INFINITY;
			double worstPriority = Double.POSITIVE_INFINITY;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				getNodeData().setEntry(q.get(j));

				priorities[j] = rule.evaluate(getNodeData());

				bestPriority = Math.max(bestPriority, priorities[j]);
				worstPriority = Math.min(worstPriority, priorities[j]);
			}

			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);

				double normPrio;
				if (bestPriority - worstPriority == 0.0) {
					normPrio = 0.0;
				} else {
					normPrio = (priorities[j] - worstPriority) / (bestPriority - worstPriority);
				}

				if (hasTracker()) {
					getTracker().addPriority(this, i, rule, entry, normPrio);
				}

				jobVotes.get(entry).addScore(normPrio);
			}
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry).getScore();
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

	@Override
	public int getNumRules() {
		return rules.size();
	}

	@Override
	public int getRuleSize(int index) {
		return rules.get(index).getSize();
	}

	@Override
	public void clear() {
		jobVotes.clear();
		jobRankings.clear();
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		Collections.sort(jobRankings);

		return jobRankings.stream().map(x -> x.getEntry()).collect(Collectors.toList());
	}

	@Override
	public void jobSelected(PrioRuleTarget entry, PriorityQueue<?> q) {
		if (hasTracker()) {
			getTracker().addStartTime(entry.getShop().simTime());
			getTracker().addSelectedEntry(this, entry);
			getTracker().addEntryRankings(this, getEntryRankings());

			clear();
		}
	}

	private class EntryVotes implements Comparable<EntryVotes> {
		private PrioRuleTarget entry;
		private double score = 0.0;

		public EntryVotes(PrioRuleTarget e) {
			entry = e;
		}

		public void addScore(double score) {
			this.score += score;
		}

		public PrioRuleTarget getEntry() {
			return entry;
		}

		public double getScore() {
			return score;
		}

		@Override
		public int compareTo(EntryVotes other) {
			if (this.score > other.score) {
				return -1;
			} else if (this.score < other.score) {
				return 1;
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
