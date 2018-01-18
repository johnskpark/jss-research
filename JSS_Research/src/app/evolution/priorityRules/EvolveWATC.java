package app.evolution.priorityRules;

import java.util.List;

import app.evolution.GPPriorityRuleBase;
import app.priorityRules.WATCPR;
import ec.Individual;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class EvolveWATC extends GPPriorityRuleBase {

	private static final long serialVersionUID = -6425099216097094953L;

	private PR rule = new WATCPR();

	@Override
	public List<Individual> getRuleComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return rule.calcPrio(entry);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
