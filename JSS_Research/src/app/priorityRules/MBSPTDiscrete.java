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
	public double calcPrio(PrioRuleTarget job) {
		double p = job.getCurrentOperation().procTime;
		if (addRepairTime(job, job.getCurrMachine(), threshold)) {
			double adjustedPrio = -(p + getMeanRepairTime(job.getCurrMachine()));

			return -(p + adjustedPrio);
		} else {
			return -p;
		}
	}

}
