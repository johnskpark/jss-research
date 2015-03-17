package app.evaluation.priorityRules;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.ATC;
import app.evaluation.AbsEvalPriorityRule;
import app.evaluation.JasimaEvalConfig;

public class EvalATC extends AbsEvalPriorityRule {

	private static final long serialVersionUID = 374528155611648088L;

	private static final double ATC_K_VALUE = 3.0;

	private double averageProcTime;

	@Override
	public void setConfiguration(JasimaEvalConfig config) {
	}
	
	private PR pr = new ATC(ATC_K_VALUE);
	
	@Override
	public double calcPrio(PrioRuleTarget entry) {
		return pr.calcPrio(entry);
	}

//	@Override
//	public void beforeCalc(PriorityQueue<?> q) {
//		averageProcTime = 0.0;
//		int numJobs = 0;
//
//		for (int i = 0; i < q.size(); i++) {
//			PrioRuleTarget entry = q.get(i);
//
//			if (arrivesTooLate(entry)) {
//				continue;
//			}
//
//			averageProcTime += entry.currProcTime();
//			numJobs++;
//		}
//		averageProcTime = averageProcTime / numJobs;
//
//		super.beforeCalc(q);
//	}
//
//	@Override
//	public double calcPrio(PrioRuleTarget entry) {
//		if (arrivesTooLate(entry)) {
//			return PriorityQueue.MIN_PRIO;
//		}
//
//		double ff = (entry.getDueDate() - entry.getRelDate()) / entry.procSum();
//		double slack = entry.getDueDate() - entry.getShop().simTime() - entry.remainingProcTime();
//
//		double remainingWaitTime = ff * (entry.remainingProcTime() - entry.currProcTime());
//
//		double prod = -Math.max((slack - remainingWaitTime) /
//				(ATC_K_VALUE * averageProcTime), 0.0);
//
//		return Math.log(entry.getWeight() / entry.currProcTime()) + prod;
//	}

}
