package app.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

/**
 * TODO javadoc. 
 * @author John
 *
 */
public class ATCPR extends PR {

	private static final long serialVersionUID = -5200383919674123645L;

	private double k;

	private double averageProcTime;

	public ATCPR(double k) {
		this.k = k;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		averageProcTime = 0.0;
		int numJobs = 0;

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);

			averageProcTime += entry.currProcTime();
			numJobs++;
		}
		averageProcTime = averageProcTime / numJobs;

		super.beforeCalc(q);
	}


	public double calcPrio(PrioRuleTarget entry) {
		if (arrivesTooLate(entry)) {
			return PriorityQueue.MIN_PRIO;
		}

		double ff = (entry.getDueDate() - entry.getRelDate()) / entry.procSum();
		double slack = entry.getDueDate() - entry.getShop().simTime() - entry.remainingProcTime();

		double remainingWaitTime = ff * (entry.remainingProcTime() - entry.currProcTime());

		double prod = -Math.max((slack - remainingWaitTime) /
				(k * averageProcTime), 0.0);

		return Math.log(entry.getWeight() / entry.currProcTime()) + prod;
	}

}
