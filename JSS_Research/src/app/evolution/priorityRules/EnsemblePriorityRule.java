package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.Arrays;
import java.util.HashMap;
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

	private Map<PrioRuleTarget, Integer> jobVotes = new HashMap<PrioRuleTarget, Integer>();
	private Pair<Double, PrioRuleTarget>[][] priorities;

	private int mostVotes;
	private PrioRuleTarget mostVotedEntry;
	private double tieBreaker; // TODO

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
		priorities = new Pair[individuals.length][q.size()];

		mostVotes = 0;
		mostVotedEntry = null;
		tieBreaker = Double.POSITIVE_INFINITY;

		for (int i = 0; i < individuals.length; i++) {
			// Calculate the priorities and find the vote of the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(i);
				data.setPrioRuleTarget(entry);

				individuals[i].trees[0].child.eval(state, threadnum, data, null, individuals[i], null);

				double priority = data.getPriority();
				priorities[i][j] = new Pair<Double, PrioRuleTarget>(priority, entry);
			}

			Arrays.sort(priorities[i]);

			// Add the vote to the pool.
			PrioRuleTarget bestEntry = priorities[i][0].item2;
			if (!jobVotes.containsKey(bestEntry)) {
				jobVotes.put(bestEntry, 0);
			}
			jobVotes.put(bestEntry, jobVotes.get(bestEntry) + 1);

			// Update the most voted job.
			int votes = jobVotes.get(bestEntry);
			if ((votes > mostVotes) ||
					(votes == mostVotes && Double.POSITIVE_INFINITY < tieBreaker)) {
				mostVotes = votes;
				mostVotedEntry = bestEntry;
				tieBreaker = Double.POSITIVE_INFINITY; // TODO
			}
		}

		// Ranking.
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO need to be able to do tie breaking.

		return jobVotes.get(entry);
	}

	private class Pair<S extends Comparable<S>, T> implements Comparable<Pair<S, T>> {
		final S item1;
		final T item2;

		public Pair(S i1, T i2) {
			item1 = i1;
			item2 = i2;
		}

		@Override
		public int compareTo(Pair<S, T> other) {
			return item1.compareTo(other.item1);
		}
	}

}
