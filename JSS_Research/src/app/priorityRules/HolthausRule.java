package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class HolthausRule extends PR {

	private static final long serialVersionUID = -7122655829024731803L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return -(2 * entry.getCurrentOperation().procTime);
		} else {
			double winq = entry.getOps()[nextTask].machine.workContent(false);
			double npt = entry.getOps()[nextTask].procTime;

			return -(2 * entry.getCurrentOperation().procTime + winq + npt);
		}
	}

}
