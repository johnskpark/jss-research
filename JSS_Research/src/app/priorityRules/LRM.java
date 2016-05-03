package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class LRM extends PR {

	private static final long serialVersionUID = -7134902769317304511L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return entry.remainingProcTime() - entry.currProcTime();
	}

}
