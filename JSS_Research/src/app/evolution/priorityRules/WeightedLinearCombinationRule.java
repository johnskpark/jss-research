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

public class WeightedLinearCombinationRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = -2159123752873667029L;

	public static final double ATC_K_VALUE = 3.0;

	private List<Individual> individuals;

	private DoubleVectorIndividual weightInd;
	private GPIndividual[] gpInds;

	private Map<PrioRuleTarget, Score> jobVotes = new HashMap<PrioRuleTarget, Score>();
	private List<Score> jobRankings = new ArrayList<Score>();

	public WeightedLinearCombinationRule() {
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

		weightInd = (DoubleVectorIndividual) individuals.get(individuals.size() -1);
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
			Score ev = new Score(q.get(i));
			jobVotes.put(q.get(i), ev);
			jobRankings.add(ev);
		}

		if (tracker != null) {
			tracker.addDispatchingDecision(q);
		}

		// Go through the individuals and vote on the decisions.
		for (int i = 0; i < gpInds.length; i++) {
			double[] priorities = new double[q.size()];
			double bestPriority = Double.NEGATIVE_INFINITY;
			double worstPriority = Double.POSITIVE_INFINITY;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				GPIndividual ind = gpInds[i];
				ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

				priorities[j] = data.getPriority();

				// Add the priority assigned to the entry to the tracker.
				if (tracker != null) {
					tracker.addPriority(this, i, gpInds[i], entry, priorities[j]);
				}

				bestPriority = Math.max(bestPriority, priorities[j]);
				worstPriority = Math.min(worstPriority, priorities[j]);
			}

			for (int j = 0; j < q.size(); j++) {
				double normPrio;
				if (bestPriority - worstPriority == 0.0) {
					normPrio = 0.0;
				} else {
					normPrio = (priorities[j] - worstPriority) / (bestPriority - worstPriority);
				}
				jobVotes.get(q.get(j)).addScore(weightInd.genome[i] * normPrio);
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
		builder.append(getClass().getSimpleName() + "[ " + individuals.get(0).genotypeToString());

		for (int i = 1; i < individuals.size(); i++) {
			builder.append("," + individuals.get(i).genotypeToString());
		}

		builder.append(" ]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		WeightedLinearCombinationRule other = (WeightedLinearCombinationRule) o;

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

	// Stores the votes made on a particular job.
	private class Score implements Comparable<Score> {
		private PrioRuleTarget entry;
		private double score = 0.0;

		public Score(PrioRuleTarget e) {
			entry = e;
		}

		public void addScore(double score) {
			this.score += score;
		}

		@SuppressWarnings("unused")
		public PrioRuleTarget getEntry() {
			return entry;
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
