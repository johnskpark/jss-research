package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

/**
 * TODO I really wish I documented where I got this from.
 * @author John
 *
 */
public class WCOVERTPR extends PR {

	private static final long serialVersionUID = 3694129880044402062L;

	private double k;

	public WCOVERTPR(double k) {
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

		double remainingWaitTime = ff * entry.remainingProcTime();
		double prod = Math.max(1.0 - Math.max(slack, 0.0) /
				(k * remainingWaitTime), 0.0);

		return entry.getWeight() / entry.currProcTime() * prod;
	}
}
