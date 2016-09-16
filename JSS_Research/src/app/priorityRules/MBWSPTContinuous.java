package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MBWSPTContinuous extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	public MBWSPTContinuous() {
		super();
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		double proc = job.getCurrentOperation().procTime;
		double prob = getProbBreakdown(job, job.getCurrMachine());

		double adjustedProc = prob * (proc + getMeanRepairTime(job.getCurrMachine())) + (1 - prob) * proc;

		if (adjustedProc > 0) {
			return job.getWeight() / adjustedProc;
		} else {
			return PriorityQueue.MAX_PRIO;
		}
	}

}
