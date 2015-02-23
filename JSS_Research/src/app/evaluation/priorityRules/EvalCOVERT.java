package app.evaluation.priorityRules;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;

public class EvalCOVERT extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 2205586413073374194L;

	private static final double COVERT_K_VALUE = 3.0;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (arrivesTooLate(entry)) {
			return PriorityQueue.MIN_PRIO;
		}

		double ff = (entry.getDueDate() - entry.getRelDate()) / entry.procSum();
		double slack = entry.getDueDate() - entry.getShop().simTime() -
				entry.remainingProcTime();

		double previousWaitTime = ff * 0.0;
		double remainingWaitTime = ff * entry.remainingProcTime();
		double totalWaitTime = previousWaitTime + remainingWaitTime;

		double prod = Math.max(1.0 - Math.max(slack, 0.0) /
				(COVERT_K_VALUE * totalWaitTime), 0.0);

		return entry.getWeight() / entry.currProcTime() * prod;
	}

}
