package app.priorityRules;

import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class MBHolthausRuleContinuous extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	public MBHolthausRuleContinuous() {
		super();
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Does nothing for this part.
	}

	@Override
	public double calcPrio(PrioRuleTarget job) {
		double p = calculateProcTime(job);
		double winq = calculateWINQ(job);
		double npt = calculateNPT(job);

		return -(2.0 * p + winq + npt);
	}

	protected double calculateProcTime(PrioRuleTarget job) {
		return calculateProcTime(job, job.getCurrMachine());
	}

	protected double calculateProcTime(PrioRuleTarget job, WorkStation machine) {
		double proc = job.getCurrentOperation().procTime;
		double prob = getProbBreakdown(job, machine);

		return (prob * (proc + getMeanRepairTime(machine)) + (1 - prob) * proc);
	}

	protected double calculateWINQ(PrioRuleTarget job) {
		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			return 0.0;
		} else {
			double adjustedWINQ = 0.0;

			WorkStation machine = job.getOps()[nextTask].machine;
			for (int i = 0; i < machine.queue.size(); i++) {
				PrioRuleTarget jobNextQueue = machine.queue.get(i);

				adjustedWINQ += calculateProcTime(jobNextQueue);
			}

			return adjustedWINQ;
		}
	}

	protected double calculateNPT(PrioRuleTarget job) {
		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			return 0.0;
		} else {
			Operation op = job.getOps()[nextTask];
			double curProcTime = job.getCurrentOperation().procTime;
			double nextProcTime = op.procTime;

			// Assume that the breakdowns are exponentially distributed.
			double meanBreakdown = getMeanBreakdown(op.machine);
			double prob =  1.0 - Math.exp(-(curProcTime + nextProcTime) / meanBreakdown);

			return (prob * (nextProcTime + getMeanRepairTime(op.machine)) + (1 - prob) * nextProcTime);
		}
	}

}
