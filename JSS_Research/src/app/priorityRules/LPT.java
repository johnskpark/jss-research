package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.SPT;

public class LPT extends PR {

	private static final long serialVersionUID = 6059848701912733541L;

	private PR spt = new SPT();

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return -spt.calcPrio(entry);
	}

}
