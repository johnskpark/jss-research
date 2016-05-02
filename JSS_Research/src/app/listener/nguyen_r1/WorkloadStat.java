package app.listener.nguyen_r1;

import java.util.ArrayList;
import java.util.List;

import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;

public class WorkloadStat {

	private int index;

	private double sumCompletedProcTime;
	private double workloadInQueue;
	private double totalRemainingWorkload;

	private List<PrioRuleTarget> jobsInQueue = new ArrayList<PrioRuleTarget>();
	private List<Operation> opsInQueue = new ArrayList<Operation>();
	private Operation minWorkloadInQueue;
	private Operation maxWorkloadInQueue;

	public WorkloadStat(int index) {
		this.index = index;
	}

	// Getters

	public int getIndex() {
		return index;
	}

	public double getSumCompletedProcTime() {
		return sumCompletedProcTime;
	}

	public double getTotalProcInQueue() {
		return workloadInQueue;
	}

	public double getTotalProcGlobal() {
		return totalRemainingWorkload;
	}

	public double getMinWorkload() {
		return (minWorkloadInQueue != null) ? minWorkloadInQueue.procTime : 0.0;
	}

	public double getMaxWorkload() {
		return (maxWorkloadInQueue != null) ? maxWorkloadInQueue.procTime : 0.0;
	}

	public List<PrioRuleTarget> getJobsInQueue() {
		return jobsInQueue;
	}

	public List<Operation> getOpInQueue() {
		return opsInQueue;
	}

	// Setters

	public void operationComplete(PrioRuleTarget job, Operation op) {
		sumCompletedProcTime += op.procTime;

		workloadInQueue -= op.procTime;
		totalRemainingWorkload -= op.procTime;

		jobsInQueue.remove(job);
		opsInQueue.remove(op);

		if (minWorkloadInQueue.equals(op)) {
			minWorkloadInQueue = opsInQueue.get(0);

			for (int i = 1; i < opsInQueue.size(); i++) {
				Operation newOp = opsInQueue.get(i);
				if (minWorkloadInQueue.procTime > newOp.procTime) {
					minWorkloadInQueue = newOp;
				}
			}
		}
		if (maxWorkloadInQueue.equals(op)) {
			maxWorkloadInQueue = opsInQueue.get(0);

			for (int i = 1; i < opsInQueue.size(); i++) {
				Operation newOp = opsInQueue.get(i);
				if (maxWorkloadInQueue.procTime < newOp.procTime) {
					maxWorkloadInQueue = newOp;
				}
			}
		}
	}

	public void operationArrivalInQueue(PrioRuleTarget job, Operation op) {
		if (jobsInQueue.contains(job) || opsInQueue.contains(op)) {
			return;
		}

		workloadInQueue += op.procTime;

		jobsInQueue.add(job);
		opsInQueue.add(op);

		if (minWorkloadInQueue == null || minWorkloadInQueue.procTime > op.procTime) {
			minWorkloadInQueue = op;
		}
		if (maxWorkloadInQueue == null || maxWorkloadInQueue.procTime < op.procTime) {
			maxWorkloadInQueue = op;
		}
	}

	public void jobArrivalInShop(Operation op) {
		totalRemainingWorkload += op.procTime;
	}



}
