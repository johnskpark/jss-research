package app.evolution.pickardt.presetRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.CR;

public class PRCR extends PR {

	private static final long serialVersionUID = 8769355098518555262L;

	public PRCR() {
		setTieBreaker(new CR());
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return entry.getWeight();
	}

}
