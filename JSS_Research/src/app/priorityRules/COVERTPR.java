package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class COVERTPR extends PR {

	private static final long serialVersionUID = 3694129880044402062L;

	private double k;

	public COVERTPR(double k) {
		this.k = k;
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (arrivesTooLate(entry)) {
			return PriorityQueue.MIN_PRIO;
		}

		double ff = (entry.getDueDate() - entry.getRelDate()) / entry.procSum();
		double slack = entry.getDueDate() - entry.getShop().simTime() -
				entry.remainingProcTime();

		double previousWaitTime = ff * 0.0; // TODO oh oh...
		double remainingWaitTime = ff * entry.remainingProcTime();
		double totalWaitTime = previousWaitTime + remainingWaitTime;

		double prod = Math.max(1.0 - Math.max(slack, 0.0) /
				(k * totalWaitTime), 0.0);

		return entry.getWeight() / entry.currProcTime() * prod;
	}
}
