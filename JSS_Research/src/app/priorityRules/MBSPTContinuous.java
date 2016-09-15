package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;

public class MBSPTContinuous extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	public MBSPTContinuous() {
		super();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		double proc = entry.getCurrentOperation().procTime;
		double prob = getProbBreakdown(entry);

		return -1.0 * (prob * (proc + getMeanRepairTime()) + (1 - prob) * proc);
	}

}
