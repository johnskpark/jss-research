package app.evaluation.priorityRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;
import app.node.INode;
import app.node.vector.DoubleVector;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class WeightedVoteRule extends AbsEvalPriorityRule {

	private static final long serialVersionUID = -3888977719103597779L;

	public static final double ATC_K_VALUE = 3.0;

	private List<INode> rules;

	private List<INode> priorityRules = new ArrayList<INode>();
	private DoubleVector weightVector;

	private Map<PrioRuleTarget, Score> jobVotes = new HashMap<PrioRuleTarget, Score>();
	private List<Score> jobRankings = new ArrayList<Score>();

	public WeightedVoteRule() {
		super();
		setTieBreaker(new ATC(ATC_K_VALUE));
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
	public List<INode> getRuleComponents() {
		return rules;
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
			int entryIndex = getVotedEntryIndex(i, q);

			PrioRuleTarget entry = q.get(entryIndex);
			jobVotes.get(entry).addWeight(weightVector.getValue(i));
		}
	}

	private int getVotedEntryIndex(int indIndex, PriorityQueue<?> q) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;
		for (int i = 0; i < q.size(); i++) {
			getNodeData().setEntry(q.get(i));

			double priority = priorityRules.get(indIndex).evaluate(getNodeData());
			if (priority > bestPriority) {
				bestPriority = priority;
				bestIndex = i;
			}
		}

		return bestIndex;
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

	public void clear() {
		jobVotes.clear();
		jobRankings.clear();
	}

	private class Score implements Comparable<Score> {
		private PrioRuleTarget entry;
		private List<Double> weights = new ArrayList<Double>();
		private double score = 0.0;

		public Score(PrioRuleTarget entry) {
			this.entry = entry;
		}

		public void addWeight(double weight) {
			weights.add(weight);
			score += weight;
		}

		@SuppressWarnings("unused")
		public PrioRuleTarget getEntry() {
			return entry;
		}

		@SuppressWarnings("unused")
		public List<Double> getWeights() {
			return weights;
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
				return 0;
			}
		}
	}

}
