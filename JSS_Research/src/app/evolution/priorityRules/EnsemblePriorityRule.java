package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

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

	private Map<PrioRuleTarget, Double> jobVotes = new HashMap<PrioRuleTarget, Double>();

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

		for (int i = 0; i < individuals.length; i++) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			PrioRuleTarget bestEntry = null;

			// Calculate the priorities and find the vote of the individual rule.
			for (int j = 0; j < q.size(); j++) {
				PrioRuleTarget entry = q.get(i);
				data.setPrioRuleTarget(entry);

				individuals[i].trees[0].child.eval(state, threadnum, data, null, individuals[i], null);

				double priority;
				if ((priority = data.getPriority()) > bestPriority) {
					bestPriority = priority;
					bestEntry = entry;
				}
			}

			// Add the vote to the pool.
			if (!jobVotes.containsKey(bestEntry)) {
				jobVotes.put(bestEntry, 0.0);
			}
			jobVotes.put(bestEntry, jobVotes.get(bestEntry) + 1);

			// TODO add tracker.
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		// TODO need to be able to do tie breaking.

		return jobVotes.get(entry);
	}

}
