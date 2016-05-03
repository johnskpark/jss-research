package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class MWKR extends PR {

	private static final long serialVersionUID = -5842837049681349607L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return entry.remainingProcTime();
	}

}
