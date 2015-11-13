package app.evolution.priorityRules;

import jasima.core.util.Pair;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.HashMap;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.coop.tracker.CoopDecisionTrackerOriginal;
import ec.gp.GPIndividual;

public class CoopPriorityRuleOriginal extends AbsGPPriorityRule {

	private static final long serialVersionUID = 1523189578600289098L;

	private GPIndividual[] individuals;

	private CoopDecisionTrackerOriginal tracker;

	private final Map<PrioRuleTarget, Pair<Integer, Double>> jobVotes = new HashMap<PrioRuleTarget, Pair<Integer, Double>>();

	public CoopPriorityRuleOriginal() {
		super();
		setTieBreaker(new TieBreaker(jobVotes));
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = config.getIndividuals();
		tracker = (CoopDecisionTrackerOriginal) config.getTracker();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		jobVotes.clear();
		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			jobVotes.put(entry, new Pair<Integer, Double>(0, 0.0));
		}

		for (int i = 0; i < individuals.length; i++) {
			double[] normalisedPriorities = getNormalisedPriorities(i, q);

			Pair<PrioRuleTarget, Double> entryPair = getBestEntry(q, normalisedPriorities);

			Pair<Integer, Double> pair = jobVotes.get(entryPair.a);
			Pair<Integer, Double> newPair = new Pair<Integer, Double>(pair.a + 1, pair.b + entryPair.b);

			jobVotes.put(entryPair.a, newPair);
		}
	}

	private Pair<PrioRuleTarget, Double> getBestEntry(PriorityQueue<?> q, double[] priorities) {
		PrioRuleTarget bestEntry = null;
		double bestPriority = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < q.size(); i++) {
			if (priorities[i] > bestPriority) {
				bestEntry = q.get(i);
				bestPriority = priorities[i];
			}
		}

		return new Pair<PrioRuleTarget, Double>(bestEntry, bestPriority);
	}

	private double[] getNormalisedPriorities(int index, PriorityQueue<?> q) {
		double[] priorities = new double[q.size()];
		double sum = 0.0;

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			data.setPrioRuleTarget(entry);

			individuals[index].trees[0].child.eval(state, threadnum, data, null, individuals[index], null);
			priorities[i] = 1.0 / (1.0 + Math.exp(data.getPriority()));
			sum += priorities[i];
		}

		for (int i = 0; i < q.size(); i++) {
			priorities[i] = (sum != 0) ? (priorities[i] / sum) : 1.0;
			tracker.addPriority(individuals[index], q.get(0).getShop().jobsFinished, priorities[i]);
		}

		return priorities;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobVotes.get(entry).a;
	}

	private class TieBreaker extends PR {
		private static final long serialVersionUID = 7658595002460736791L;

		private final Map<PrioRuleTarget, Pair<Integer, Double>> jobVotes;

		public TieBreaker(Map<PrioRuleTarget, Pair<Integer, Double>> jv) {
			this.jobVotes = jv;
		}

		@Override
		public double calcPrio(PrioRuleTarget entry) {
			return jobVotes.get(entry).b;
		}
	}

}
