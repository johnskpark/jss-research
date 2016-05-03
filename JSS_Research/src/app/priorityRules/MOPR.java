package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class MOPR extends PR {

	private static final long serialVersionUID = -3938114189669682564L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return entry.numOpsLeft();
	}

}
