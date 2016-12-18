package app.listener.hunt;

import java.util.LinkedList;
import java.util.Queue;

import app.JasimaWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class HuntListener extends JasimaWorkStationListener {

	private int maxSize;

	private OperationStartStat[] startedJobs;
	private Queue<OperationCompletionStat>[] completedJobs;
	private int numMachines;

	private double sumWaitTimes = 0.0;
	private int numCompleted = 0;

	public HuntListener(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	protected void operationCompleted(WorkStation m, PrioRuleTarget justCompleted) {
		int index = m.index();

		if (startedJobs[index].entry != justCompleted) {
			throw new RuntimeException("The job selected to be processed on the machine does not match with the completed job");
		}

		OperationStartStat startStat = startedJobs[index];

		OperationCompletionStat stat = new OperationCompletionStat(startStat.entry);
		stat.setArrivalTime(startStat.arrivalTime);
		stat.setStartTime(startStat.startTime);
		stat.setWaitTime(startStat.startTime - startStat.arrivalTime);
		stat.setCompletionTime(startStat.entry.getShop().simTime());

		Queue<OperationCompletionStat> queue = completedJobs[index];
		if (queue.size() == maxSize) {
			OperationCompletionStat oldStat = queue.poll();

			sumWaitTimes -= oldStat.getWaitTime();
			numCompleted--;
		}

		queue.offer(stat);

		sumWaitTimes += stat.getWaitTime();
		numCompleted++;

		startedJobs[index] = null;
	}

	@Override
	protected void operationStarted(WorkStation m,
			PrioRuleTarget justStarted,
			int oldSetupState,
			int newSetupState,
			double setupTime) {
		int index = m.index();

		if (startedJobs[index] != null) {
			throw new RuntimeException("The machine should not currently be processing any jobs");
		}

		OperationStartStat stat = new OperationStartStat();
		stat.entry = justStarted;
		stat.arrivalTime = stat.entry.getArriveTime();
		stat.startTime = stat.entry.getShop().simTime();

		startedJobs[index] = stat;
	}

	@Override
	protected void init(WorkStation m) {
		numMachines = m.shop().machines.length;

		clear();
	}

	public boolean hasCompletedJobs(WorkStation machine) {
		return !completedJobs[machine.index()].isEmpty();
	}

	public Queue<OperationCompletionStat> getLastCompletedJobs(WorkStation machine) {
		return completedJobs[machine.index()];
	}

	public double getAverageWaitTimesAllMachines() {
		return (sumWaitTimes != 0.0) ? (sumWaitTimes / numCompleted) : 0.0;
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		startedJobs = new OperationStartStat[numMachines];
		completedJobs = new Queue[numMachines];
		for (int i = 0; i < numMachines; i++) {
			completedJobs[i] = new LinkedList<OperationCompletionStat>();
		}

		sumWaitTimes = 0.0;
		numCompleted = 0;
	}

	private class OperationStartStat {
		PrioRuleTarget entry;

		double arrivalTime;
		double startTime;
	}

}
