package app.evolution.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.gp.GPIndividual;
import ec.util.Pair;

public class BasicPriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = 5215861545303707980L;

	private GPIndividual[] individual;

	private int indIndex;

	private Map<PrioRuleTarget, Double> jobPriorities = new HashMap<PrioRuleTarget, Double>();
	private List<Pair<PrioRuleTarget, Double>> jobRankings = new ArrayList<Pair<PrioRuleTarget, Double>>();

	private JobComparator comparator = new JobComparator();

	public BasicPriorityRule() {
		super();
		indIndex = 0;
	}

	public BasicPriorityRule(int indIndex) {
		super();
		this.indIndex = indIndex;
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individual = config.getIndividuals();
	}

	@Override
	public GPIndividual[] getIndividuals() {
		return individual;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();

		if (tracker != null) {
			tracker.addDispatchingDecision(q);
		}

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);

			data.setPrioRuleTarget(entry);

			individual[indIndex].trees[0].child.eval(state, threadnum, data, null, individual[indIndex], null);

			double priority = data.getPriority();

			// Add the priority assigned to the entry to the tracker.
			if (tracker != null) {
				tracker.addPriority(i, individual[indIndex], entry, priority);
			}

			jobPriorities.put(entry, priority);
			jobRankings.add(new Pair<PrioRuleTarget, Double>(entry, priority));
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobPriorities.get(entry);
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// Sort the list of jobs.
		Collections.sort(jobRankings, comparator);

		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
		for (Pair<PrioRuleTarget, Double> e : jobRankings) {
			entries.add(e.i1);
		}

		return entries;
	}

	@Override
	public void clear() {
		jobPriorities.clear();
		jobRankings.clear();
	}

	private class JobComparator implements Comparator<Pair<PrioRuleTarget, Double>> {

		@Override
		public int compare(Pair<PrioRuleTarget, Double> o1,
				Pair<PrioRuleTarget, Double> o2) {
			if (o1.i2 > o2.i2) {
				return -1;
			} else if (o1.i2 < o2.i2) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
