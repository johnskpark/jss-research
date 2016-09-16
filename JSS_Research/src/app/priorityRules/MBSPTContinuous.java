package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;

public class MBSPTContinuous extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	public MBSPTContinuous() {
		super();
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		double proc = job.getCurrentOperation().procTime;
		double prob = getProbBreakdown(job, job.getCurrMachine());

		return -1.0 * (prob * (proc + getMeanRepairTime(job.getCurrMachine())) + (1 - prob) * proc);
	}

}
