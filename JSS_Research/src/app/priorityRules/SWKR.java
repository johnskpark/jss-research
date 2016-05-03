package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class SWKR extends PR {

	private static final long serialVersionUID = -1247843698401144516L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return -entry.remainingProcTime();
	}

}
