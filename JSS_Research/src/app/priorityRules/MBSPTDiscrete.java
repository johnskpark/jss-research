package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;

public class MBSPTDiscrete extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private double threshold;

	public MBSPTDiscrete(double threshold) {
		super();

		this.threshold = threshold;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		double p = entry.getCurrentOperation().procTime;
		if (addRepairTime(entry, threshold)) {
			double adjustedPrio = -(p + getMeanRepairTime());

			return -(p + adjustedPrio);
		} else {
			return -p;
		}
	}

}
