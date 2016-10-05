package app.priorityRules;

import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class MBWHolthausRuleDiscrete2 extends MBPR {

	private static final long serialVersionUID = 3376768221888266245L;

	public MBWHolthausRuleDiscrete2() {
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

		return job.getWeight() / (2.0 * p + winq + npt);
	}

	protected double calculateProcTime(PrioRuleTarget job) {
		return calculateProcTime(job, job.getCurrMachine());
	}

	protected double calculateProcTime(PrioRuleTarget job, WorkStation machine) {
		double proc = job.getCurrentOperation().procTime;
		if (job.getShop().simTime() + proc <= getNextBreakdown(machine)) {
			return proc;
		} else {
			return proc + getMeanRepairTime(machine);
		}
	}

	protected double calculateWINQ(PrioRuleTarget job) {
		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			return 0.0;
		} else {
			double adjustedWINQ = 0.0;

			WorkStation machine = job.getOps()[nextTask].machine;
			for (int i = 0; i < machine.queue.size(); i++) {
				adjustedWINQ += calculateProcTime(machine.queue.get(i));
			}

			IndividualMachine indMachine = machine.machDat()[0];
			if (indMachine.procFinished > job.getShop().simTime()) {
				// Calculate the work remaining on the current job being processed.
				double workRemaining;
				if (indMachine.procFinished <= getNextBreakdown(machine)) {
					workRemaining = indMachine.procFinished - job.getShop().simTime();
				} else {
					workRemaining = indMachine.procFinished - job.getShop().simTime() + getMeanRepairTime(machine);
				}
				adjustedWINQ += workRemaining;
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
			double curProcTime = calculateProcTime(job);
			double nextProcTime = op.procTime;

			if (job.getShop().simTime() + curProcTime + nextProcTime <= getNextBreakdown(op.machine)) {
				return nextProcTime;
			} else {
				return nextProcTime + getMeanRepairTime(op.machine);
			}
		}
	}

}