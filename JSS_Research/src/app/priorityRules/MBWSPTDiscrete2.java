package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.prioRules.weighted.WSPT;

public class MBWSPTDiscrete2 extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private boolean withinFilter = false;
	private PR wspt = new WSPT();

	public MBWSPTDiscrete2() {
		super();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		for (int i = 0; i < q.size() && !withinFilter; i++) {
			PrioRuleTarget job = q.get(i);
			double p = job.getCurrentOperation().procTime;

			if (job.getShop().simTime() + p < getNextBreakdown(job.getCurrMachine())) {
				withinFilter = true;
			}
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		double p = job.getCurrentOperation().procTime;
		if (!withinFilter || job.getShop().simTime() + p < getNextBreakdown(job.getCurrMachine())) {
			return wspt.calcPrio(job);
		} else {
			double adjustedP = p + getMeanRepairTime(job.getCurrMachine());
			if (adjustedP > 0) {
				return job.getWeight() / adjustedP;
			} else {
				return PriorityQueue.MAX_PRIO;
			}
		}
	}

}
