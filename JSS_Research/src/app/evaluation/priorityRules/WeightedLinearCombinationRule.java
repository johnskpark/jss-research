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
import app.node.vector.DoubleVector;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class WeightedLinearCombinationRule extends EvalPriorityRuleBase {

	private static final long serialVersionUID = -3888977719103597779L;

	public static final double ATC_K_VALUE = 3.0;

	private List<INode> rules;

	private List<INode> priorityRules = new ArrayList<INode>();
	private DoubleVector weightVector;

	private Map<PrioRuleTarget, Score> jobVotes = new HashMap<PrioRuleTarget, Score>();
	private List<Score> jobRankings = new ArrayList<Score>();

	public WeightedLinearCombinationRule() {
		super();
		setTieBreaker(new ATC(ATC_K_VALUE));
	}

	@Override
	public List<INode> getRuleComponents() {
		return priorityRules;
	}

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
		setSeed(config.getSeed());
		setNodeData(config.getNodeData());

		rules = config.getRules();

		INode lastRule = rules.get(rules.size() - 1);
		if (!(lastRule instanceof DoubleVector)) {
			throw new RuntimeException("The last rule must be a DoubleVector type.");
		}

		weightVector = (DoubleVector) lastRule;

		for (int i = 0; i < rules.size() - 1; i++) {
			priorityRules.add(rules.get(i));
		}
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Clear and repopulate the job votes.
		clear();
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			Score score = new Score(entry);
			jobVotes.put(entry, score);
			jobRankings.add(score);
		}

		// Add weights to the voted jobs.
		for (int i = 0; i < priorityRules.size(); i++) {
			INode rule = priorityRules.get(i);

			double[] priorities = new double[q.size()];
			double bestPriority = Double.NEGATIVE_INFINITY;
			double worstPriority = Double.POSITIVE_INFINITY;

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

				jobVotes.get(entry).addScore(weightVector.getValue(i) * normPrio);
			}
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry).getScore();
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
	public List<PrioRuleTarget> getEntryRankings() {
		Collections.sort(jobRankings);

		return jobRankings.stream().map(x -> x.entry).collect(Collectors.toList());
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

	@Override
	public void clear() {
		jobVotes.clear();
		jobRankings.clear();
	}

	private class Score implements Comparable<Score> {
		private PrioRuleTarget entry;
		private double score = 0.0;

		public Score(PrioRuleTarget entry) {
			this.entry = entry;
		}

		public void addScore(double score) {
			this.score += score;
		}

		public double getScore() {
			return score;
		}

		@Override
		public int compareTo(Score other) {
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
