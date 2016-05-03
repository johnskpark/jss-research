package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class LSO extends PR {

	private static final long serialVersionUID = 6670634748446616835L;

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		int nextTask = entry.getTaskNumber() + 1;

		if (nextTask >= entry.numOps()) {
			return 0.0;
		} else {
			return entry.getOps()[nextTask].procTime;
		}
	}

}
