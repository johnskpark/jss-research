package app.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class MBWSPTFilter extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	private boolean withinFilter = false;

	public MBWSPTFilter() {
		super();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		for (int i = 0; i < q.size() && !withinFilter; i++) {
			PrioRuleTarget job = q.get(i);
			double p = job.getCurrentOperation().procTime;

			if (job.getShop().simTime() + p < getNextBreakdown()) {
				withinFilter = true;
			}
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		double p = job.getCurrentOperation().procTime;
		if (!withinFilter || job.getShop().simTime() + p < getNextBreakdown()) {
			if (p > 0) {
				return job.getWeight() / p;
			} else {
				return PriorityQueue.MAX_PRIO;
			}
		} else {
			return PriorityQueue.MAX_PRIO;
		}
	}

}
