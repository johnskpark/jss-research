package app.listener.nguyen_r1;

import java.util.ArrayList;
import java.util.List;

import jasima.shopSim.core.Operation;

public class WorkloadStat {

	private int index;

	private double sumCompletedProcTime;
	private double workloadInQueue;
	private double totalRemainingWorkload;

	private List<Operation> operationInQueue = new ArrayList<Operation>();
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

	public double getWorkloadInQueue() {
		return workloadInQueue;
	}

	public double getTotalRemainingWorkload() {
		return totalRemainingWorkload;
	}

	public double getMinWorkload() {
		return (minWorkloadInQueue != null) ? minWorkloadInQueue.procTime : 0.0;
	}

	public double getMaxWorkload() {
		return (maxWorkloadInQueue != null) ? maxWorkloadInQueue.procTime : 0.0;
	}

	// Setters

	public void operationComplete(Operation op) {
		sumCompletedProcTime += op.procTime;

		workloadInQueue -= op.procTime;
		totalRemainingWorkload -= op.procTime;

		operationInQueue.remove(op);
		if (minWorkloadInQueue.equals(op)) {
			minWorkloadInQueue = null;

			for (int i = 0; i < operationInQueue.size(); i++) {
				Operation newOp = operationInQueue.get(i);
				if (minWorkloadInQueue == null || minWorkloadInQueue.procTime > newOp.procTime) {
					minWorkloadInQueue = newOp;
				}
			}
		}
		if (maxWorkloadInQueue.equals(op)) {
			maxWorkloadInQueue = null;

			for (int i = 0; i < operationInQueue.size(); i++) {
				Operation newOp = operationInQueue.get(i);
				if (maxWorkloadInQueue == null || maxWorkloadInQueue.procTime < newOp.procTime) {
					maxWorkloadInQueue = newOp;
				}
			}
		}
	}

	public void operationArrivalInQueue(Operation op) {
		if (operationInQueue.contains(op)) {
			return;
		}

		workloadInQueue += op.procTime;

		operationInQueue.add(op);
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
