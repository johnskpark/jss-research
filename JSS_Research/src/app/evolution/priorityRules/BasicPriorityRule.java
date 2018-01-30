package app.evolution.priorityRules;

import java.util.Arrays;
import java.util.List;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class BasicPriorityRule extends GPPriorityRuleBase {

	private static final long serialVersionUID = 5215861545303707980L;

	private List<Individual> individual;

	private int indIndex;

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
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setPrioRuleTarget(entry);

		GPIndividual ind = (GPIndividual) individual.get(indIndex);
		ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

		return data.getPriority();
	}

	@Override
	public void clear() {
		// Does nothing for now.
	}

}
