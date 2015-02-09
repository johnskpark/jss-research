package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsPriorityRule;
import app.evolution.JasimaGPConfiguration;
import app.evolution.JasimaGPData;
import app.evolution.tracker.DecisionTracker;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class EnsemblePriorityRule extends AbsPriorityRule {

	private static final long serialVersionUID = -2159123752873667029L;

	private EvolutionState state;
	private GPIndividual[] individuals;
	private int threadnum;

	private JasimaGPData data;
	private DecisionTracker tracker;

	private Map<PrioRuleTarget, EntryVotes> jobVotes = new HashMap<PrioRuleTarget, EntryVotes>();
	private List<EntryVotes> jobRanking = new ArrayList<EntryVotes>();

	@Override
	public void setConfiguration(JasimaGPConfiguration config) {
		state = config.getState();
		individuals = config.getIndividuals();
		data = config.getData();
		tracker = (DecisionTracker) config.getTracker();
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

		int[] decisions = new int[individuals.length];

		// TODO make this more efficient later down the line, but first get the functionality working.
		for (int i = 0; i < individuals.length; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

			// Find the job selected by the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(j);
				data.setPrioRuleTarget(entry);

				individuals[i].trees[0].child.eval(state, threadnum, data, null, individuals[i], null);

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

		// Sort the list of jobs.
		Collections.sort(jobRanking);

		// Add the rankings into the decisions.
		for (int i = 0; i < individuals.length; i++) {
			for (int j = 0; j < q.size(); j++) {
				EntryVotes sc = jobRanking.get(j);
				if (decisions[i] == sc.index) {
					tracker.addDecision(individuals[i], j);
				}
			}
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return (entry.equals(jobRanking.get(0).entry)) ? 1.0 : 0.0;
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

		@Override
		public int compareTo(EntryVotes other) {
			int diff = this.count - other.count;
			if (diff != 0) {
				return diff;
			} else {
				// Use SPT for now.
				if (this.entry.currProcTime() < other.entry.currProcTime()) {
					return -1;
				} else if (this.entry.currProcTime() > other.entry.currProcTime()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

}
