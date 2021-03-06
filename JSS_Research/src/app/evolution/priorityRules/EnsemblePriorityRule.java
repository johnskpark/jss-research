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
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.basic.ATC;

public class EnsemblePriorityRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = -2159123752873667029L;

	public static final double ATC_K_VALUE = 3.0;

	private List<Individual> individuals;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRankings = new ArrayList<EntryVotes>();

	public EnsemblePriorityRule() {
		super();
		setTieBreaker(new ATC(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = Arrays.asList(config.getIndividuals());
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
			EntryVotes ev = new EntryVotes(q.get(i));
			jobVotes.put(q.get(i), ev);
			jobRankings.add(ev);
		}

		// Initialise the decisions vector.
		int[] decisions = new int[individuals.size()];

		// Go through the individuals and vote on the decisions.
		for (int i = 0; i < individuals.size(); i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				GPIndividual ind = (GPIndividual) individuals.get(i);
				ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

				double priority = data.getPriority();

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

		EnsemblePriorityRule other = (EnsemblePriorityRule) o;

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
	private class EntryVotes implements Comparable<EntryVotes> {
		private PrioRuleTarget entry;
		private int count = 0;

		public EntryVotes(PrioRuleTarget e) {
			entry = e;
		}

		public void increment() {
			count++;
		}

		@SuppressWarnings("unused")
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
