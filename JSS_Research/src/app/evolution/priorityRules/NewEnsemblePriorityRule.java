package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.priorityRules.ATCPR;

public class NewEnsemblePriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = 1763515401337543392L;

	public static final double ATC_K_VALUE = 3.0;

	private AbsGPPriorityRule[] constituentRules;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRanking = new ArrayList<EntryVotes>();

	public NewEnsemblePriorityRule() {
		super();
		setTieBreaker(new ATCPR(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		int ruleNum = config.getIndividuals().length;

		constituentRules = new AbsGPPriorityRule[config.getIndividuals().length];

		for (int i = 0; i < ruleNum; i++) {
			constituentRules[i] = new BasicPriorityRule(i);
			constituentRules[i].setConfiguration(config);
		}
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		jobVotes.clear();
		jobRanking.clear();

		for (int i = 0; i < q.size(); i++) {
			EntryVotes ev = new EntryVotes(i, q.get(i));
			jobVotes.put(q.get(i), ev);
			jobRanking.add(ev);
		}

		int[] decisions = new int[constituentRules.length];

		for (int i = 0; i < constituentRules.length; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);

				double priority = constituentRules[i].calcPrio(entry);
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

		// Sort the list of jobs.
		Collections.sort(jobRanking);

		// Add the rankings into the decisions.
		if (tracker != null) {
			// TODO
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry).getCount();
	}

	// Stores the votes made on a particular job.
	private class EntryVotes implements Comparable<EntryVotes> {
		final int index;
		final PrioRuleTarget entry;
		private int count = 0;

		public EntryVotes(int i, PrioRuleTarget e) {
			index = i;
			entry = e;
		}

		public void increment() {
			count++;
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
