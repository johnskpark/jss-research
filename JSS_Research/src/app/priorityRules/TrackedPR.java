package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class TrackedPR extends PR {

	private static final long serialVersionUID = -6359385279252431755L;

	private PR referenceRule = null;
	private boolean firstRun = true;

	private PR[] priorityRules = null;

	public TrackedPR(PR refRule) {
		super();

		referenceRule = refRule;
	}

	public boolean isFirstRun() {
		return firstRun;
	}

	public void setFirstRun(boolean firstRun) {
		this.firstRun = firstRun;
	}

	public PR[] getPriorityRules() {
		return priorityRules;
	}

	public void setPriorityRules(PR[] priorityRules) {
		this.priorityRules = priorityRules;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		if (firstRun) {
			// TODO
		} else {
			// TODO
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return referenceRule.calcPrio(entry);
	}

}
