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
import ec.gp.GPIndividual;

public class EnsemblePriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -2159123752873667029L;

	public static final double ATC_K_VALUE = 3.0;

	private GPIndividual[] individuals;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRankings = new ArrayList<EntryVotes>();

	public EnsemblePriorityRule() {
		super();
		setTieBreaker(new ATCPR(ATC_K_VALUE));
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = config.getIndividuals();
	}

	@Override
	public GPIndividual[] getIndividuals() {
		return individuals;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clearRankings();

		for (int i = 0; i < q.size(); i++) {
			EntryVotes ev = new EntryVotes(q.get(i));
			jobVotes.put(q.get(i), ev);
			jobRankings.add(ev);
		}

		int[] decisions = new int[individuals.length];

		if (tracker != null) {
			tracker.addDispatchingDecision(q);
		}

		for (int i = 0; i < individuals.length; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				individuals[i].trees[0].child.eval(state, threadnum, data, null, individuals[i], null);

				double priority = data.getPriority();

				// Add the priority assigned to the entry to the tracker.
				if (tracker != null) {
					tracker.addPriority(i, individuals[i], entry, priority);
				}

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
	public List<PrioRuleTarget> getEntryRankings() {
		// Sort the list of jobs.
		Collections.sort(jobRankings);

		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
		for (EntryVotes e : jobRankings) {
			entries.add(e.entry);
		}

		return entries;
	}

	@Override
	public void clearRankings() {
		jobVotes.clear();
		jobRankings.clear();
	}

	// Stores the votes made on a particular job.
	private class EntryVotes implements Comparable<EntryVotes> {
		final PrioRuleTarget entry;
		private int count = 0;

		public EntryVotes(PrioRuleTarget e) {
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
