package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.weighted.WSPT;

public class MBWSPTDiscrete extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private double threshold;
	private PR wspt = new WSPT();

	public MBWSPTDiscrete(double threshold) {
		super();

		this.threshold = threshold;
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		if (addRepairTime(job, job.getCurrMachine(), threshold)) {
			double p = job.getCurrentOperation().procTime;
			if (p + getMeanRepairTime(job.getCurrMachine()) > 0) {
				return job.getWeight() / (p + getMeanRepairTime(job.getCurrMachine()));
			} else {
				return PriorityQueue.MAX_PRIO;
			}
		} else {
			return wspt.calcPrio(job);
		}
	}

}
