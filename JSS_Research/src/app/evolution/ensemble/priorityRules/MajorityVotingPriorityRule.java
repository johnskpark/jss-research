package app.evolution.ensemble.priorityRules;

import jasima.core.util.Pair;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MajorityVotingPriorityRule extends AbsGPEnsemblePriorityRule {

	private static final long serialVersionUID = -7568856538005167311L;

	private Map<PrioRuleTarget, Integer> jobVotes = new HashMap<PrioRuleTarget, Integer>();

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		trackerValue.clear();
		jobVotes.clear();

		trackerValue.setIndividuals(individuals);
		trackerValue.setEntries(getEntries(q));

		for (int i = 0; i < q.size(); i++) {
			jobVotes.put(q.get(i), 0);
		}

		double[][] priorities = new double[individuals.length][q.size()];
		for (int i = 0; i < individuals.length; i++) {
			priorities[i] = getPriorities(i, q);
			PrioRuleTarget bestEntry = getBestEntry(q, priorities[i]);
			jobVotes.put(bestEntry, jobVotes.get(bestEntry) + 1);
		}

		@SuppressWarnings("unchecked")
		Pair<Integer, Integer>[] jobIndexVotePairs = new Pair[q.size()];
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			jobIndexVotePairs[i] = new Pair<Integer, Integer>(i+1, jobVotes.get(entry));
		}
		Arrays.sort(jobIndexVotePairs, new VoteComparator(q));

		int[] ensembleRanking = new int[q.size()];
		for (int i = 0; i < q.size(); i++) {
			ensembleRanking[i] = jobIndexVotePairs[i].a;
		}

		tracker.addTrackerValue(q.get(0).getShop().jobsFinished, trackerValue);
	}

	private PrioRuleTarget[] getEntries(PriorityQueue<?> q) {
		PrioRuleTarget[] entries = new PrioRuleTarget[q.size()];
		for (int i = 0; i < q.size(); i++) {
			entries[i] = q.get(i);
		}
		return entries;
	}

	private double[] getPriorities(int index, PriorityQueue<?> q) {
		double[] priorities = new double[q.size()];
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			data.setPrioRuleTarget(entry);

			individuals[index].trees[0].child.eval(state, threadnum, data, null, individuals[index], null);
			priorities[i] = data.getPriority();
		}
		return priorities;
	}

	private PrioRuleTarget getBestEntry(PriorityQueue<?> q, double[] priorities) {
		PrioRuleTarget bestEntry = null;
		double bestPriority = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < q.size(); i++) {
			if (priorities[i] > bestPriority) {
				bestEntry = q.get(i);
				bestPriority = priorities[i];
			}
		}
		return bestEntry;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry);
	}

	private class VoteComparator implements Comparator<Pair<Integer, Integer>> {

		private PriorityQueue<?> q;

		public VoteComparator(PriorityQueue<?> q) {
			this.q = q;
		}

		@Override
		public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
			int diff = o2.b - o1.b;
			if (diff != 0 || getTieBreaker() == null) {
				return diff;
			}

			PR tieBreaker = getTieBreaker();
			double prio1 = tieBreaker.calcPrio(q.get(o1.a));
			double prio2 = tieBreaker.calcPrio(q.get(o2.a));

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
