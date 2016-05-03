package app.evolution.priorityRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.vector.DoubleVectorIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class WeightedLinearCombinationRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -2159123752873667029L;

	public static final double ATC_K_VALUE = 3.0;

	private Individual[] individuals;

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

		individuals = config.getIndividuals();

		if (individuals.length <= 1) {
			throw new RuntimeException("There must be one vector individual and at least one GP individual.");
		}

		weightInd = (DoubleVectorIndividual) individuals[individuals.length -1];
		if (weightInd.genome.length != individuals.length - 1) {
			throw new RuntimeException("The length of the first individual must be equal to the number of GP individuals.");
		}

		gpInds = new GPIndividual[individuals.length - 1];
		for (int i = 0; i < individuals.length - 1; i++) {
			gpInds[i] = (GPIndividual) individuals[i];
		}
	}

	@Override
	public Individual[] getIndividuals() {
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
		for (int i = 0; i < individuals.length; i++) {
			double[] priorities = new double[q.size()];
			double bestPriority = Double.NEGATIVE_INFINITY;
			double worstPriority = Double.POSITIVE_INFINITY;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				GPIndividual ind = (GPIndividual) individuals[i];
				ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

				priorities[j] = data.getPriority();

				// Add the priority assigned to the entry to the tracker.
				if (tracker != null) {
					tracker.addPriority(i, individuals[i], entry, priorities[j]);
				}

				bestPriority = Math.max(bestPriority, priorities[j]);
				worstPriority = Math.min(worstPriority, priorities[j]);
			}

			for (int j = 0; j < q.size(); j++) {
				double normPrio = (priorities[j] - worstPriority) / (bestPriority - worstPriority);
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
		builder.append(getClass().getSimpleName() + "[ " + individuals[0].genotypeToString());

		for (int i = 1; i < individuals.length; i++) {
			builder.append("," + individuals[i].genotypeToString());
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

		if (this.individuals.length != other.individuals.length) {
			return false;
		}

		for (int i = 0; i < this.individuals.length; i++) {
			if (!this.individuals[i].equals(other.individuals[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// Sort the list of jobs.
		Collections.sort(jobRankings);

		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
		for (Score e : jobRankings) {
			entries.add(e.entry);
		}

		return entries;
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
