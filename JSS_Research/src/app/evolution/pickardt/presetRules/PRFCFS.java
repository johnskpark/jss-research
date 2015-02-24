package app.evolution.pickardt.presetRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.FCFS;

public class PRFCFS extends PR {

	private static final long serialVersionUID = 1887717590022139151L;

	public PRFCFS() {
		setTieBreaker(new FCFS());
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return entry.getWeight();
	}

}
