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
import ec.util.Pair;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class BasicPriorityRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = 5215861545303707980L;

	private List<Individual> individual;

	private int indIndex;

	private Map<PrioRuleTarget, Double> jobPriorities = new HashMap<PrioRuleTarget, Double>();
	private List<Pair<PrioRuleTarget, Double>> jobRankings = new ArrayList<Pair<PrioRuleTarget, Double>>();

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

		individual = Arrays.asList(config.getIndividuals());
	}

	@Override
	public List<Individual> getRuleComponents() {
		return individual;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		clear();

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);

			data.setPrioRuleTarget(entry);

			GPIndividual ind = (GPIndividual) individual.get(indIndex);
			ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

			double priority = data.getPriority();

			jobPriorities.put(entry, priority);
			jobRankings.add(new Pair<PrioRuleTarget, Double>(entry, priority));
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return jobPriorities.get(entry);
	}

	@Override
	public void clear() {
		jobPriorities.clear();
		jobRankings.clear();
	}

}
