package app.evolution.priorityRules;

import java.util.ArrayList;
import java.util.List;

import app.evolution.GPPriorityRuleBase;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.weighted.WSPT;

public class EvolveWSPT extends GPPriorityRuleBase {

	private static final long serialVersionUID = -6425099216097094953L;

	private PR rule = new WSPT();

	private List<Individual> dummyRuleComponent;

	public EvolveWSPT() {
		dummyRuleComponent = new ArrayList<Individual>();
		dummyRuleComponent.add(new GPIndividual());
	}

	@Override
	public List<Individual> getRuleComponents() {
		return dummyRuleComponent;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		rule.beforeCalc(q);
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return rule.calcPrio(entry);
	}

	@Override
	public void clear() {
		// Don't need to do anything here, nothing is stored.
	}

}
