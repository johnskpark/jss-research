package app.evolution.priorityRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.vector.DoubleVectorIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class WeightedVoteRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = 7888792734728411775L;

	public static final double ATC_K_VALUE = 3.0;

	private List<Individual> individuals;

	private DoubleVectorIndividual weightInd;
	private GPIndividual[] gpInds;

	private Map<Integer, Score> jobVotes = new HashMap<Integer, Score>();
	private List<Score> jobRankings = new ArrayList<Score>();

	public WeightedVoteRule() {
		super();
		setTieBreaker(new ATC(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = Arrays.asList(config.getIndividuals());

		if (individuals.size() <= 1) {
			throw new RuntimeException("There must be one vector individual and at least one GP individual.");
		}

		weightInd = (DoubleVectorIndividual) individuals.get(individuals.size() - 1);
		if (weightInd.genome.length != individuals.size() - 1) {
			throw new RuntimeException("The length of the first individual must be equal to the number of GP individuals.");
		}

		gpInds = new GPIndividual[individuals.size() - 1];
		for (int i = 0; i < individuals.size() - 1; i++) {
			gpInds[i] = (GPIndividual) individuals.get(i);
		}
	}

	@Override
	public List<Individual> getRuleComponents() {
		return individuals;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Clear and repopulate the job votes.
		clear();
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			Score score = new Score(entry);
			jobVotes.put(entry.getJobNum(), score);
			jobRankings.add(score);
		}

		// TODO add in the tracking later down the line.

		// Add weights to the voted jobs.
		for (int i = 0; i < gpInds.length; i++) {
			int entryIndex = getVotedEntryIndex(i, q);

			PrioRuleTarget entry = q.get(entryIndex);
			jobVotes.get(entry.getJobNum()).addWeight(weightInd.genome[i]);
		}
	}

	private int getVotedEntryIndex(int indIndex, PriorityQueue<?> q) {
		GPIndividual ind = gpInds[indIndex];

		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			data.setPrioRuleTarget(entry);

			ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

			double priority = data.getPriority();
			if (priority > bestPriority) {
				bestPriority = priority;
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry.getJobNum()).getScore();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		WeightedVoteRule other = (WeightedVoteRule) o;

		if (this.individuals.size() != other.individuals.size()) {
			return false;
		}

		for (int i = 0; i < this.individuals.size(); i++) {
			if (!this.individuals.get(i).equals(other.individuals.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
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
