package app.listener.hunt;

import java.util.LinkedList;
import java.util.Queue;

import app.JasimaWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

public class HuntListener extends JasimaWorkStationListener {

	private ReferenceStat stat = new ReferenceStat();

	public HuntListener(int maxSize) {
		super();

		this.stat.maxSize = maxSize;
	}

	@Override
	protected void operationCompleted(WorkStation m, PrioRuleTarget justCompleted) {
		int index = m.index();

		if (stat.startedJobs[index].entry != justCompleted) {
			throw new RuntimeException("The job selected to be processed on the machine does not match with the completed job");
		}

		OperationStartStat startStat = stat.startedJobs[index];

		OperationCompletionStat stat = new OperationCompletionStat(startStat.entry);
		stat.setArrivalTime(startStat.arrivalTime);
		stat.setStartTime(startStat.startTime);
		stat.setWaitTime(startStat.startTime - startStat.arrivalTime);
		stat.setCompletionTime(startStat.entry.getShop().simTime());

		Queue<OperationCompletionStat> queue = this.stat.completedJobs[index];
		if (queue.size() == this.stat.maxSize) {
			OperationCompletionStat oldStat = queue.poll();

			this.stat.sumWaitTimes -= oldStat.getWaitTime();
			this.stat.sumWaitTimesPerMachine[m.index()] -= oldStat.getWaitTime();
			this.stat.numCompleted--;
		}

		queue.offer(stat);

		this.stat.sumWaitTimes += stat.getWaitTime();
		this.stat.sumWaitTimesPerMachine[m.index()] += stat.getWaitTime();
		this.stat.numCompleted++;

		this.stat.startedJobs[index] = null;
	}

	@Override
	protected void operationStarted(WorkStation m,
			PrioRuleTarget justStarted,
			int oldSetupState,
			int newSetupState,
			double setupTime) {
		int index = m.index();

		if (stat.startedJobs[index] != null) {
			throw new RuntimeException("The machine should not currently be processing any jobs");
		}

		OperationStartStat stat = new OperationStartStat();
		stat.entry = justStarted;
		stat.arrivalTime = stat.entry.getArriveTime();
		stat.startTime = stat.entry.getShop().simTime();

		this.stat.startedJobs[index] = stat;
	}

	@Override
	protected void init(WorkStation m) {
		stat.numMachines = m.shop().machines.length;
		stat.initialised = true;

		clear();
	}

	public boolean hasCompletedJobs(WorkStation machine) {
		if (!stat.initialised) {
			throw new RuntimeException("The listener has not yet been stat.initialised!");
		}

		return !stat.completedJobs[machine.index()].isEmpty();
	}

	public Queue<OperationCompletionStat> getLastCompletedJobs(WorkStation machine) {
		if (!stat.initialised) {
			throw new RuntimeException("The listener has not yet been stat.initialised!");
		}

		return stat.completedJobs[machine.index()];
	}

	public double getAverageWaitTime(WorkStation machine) {
		if (!stat.initialised) {
			throw new RuntimeException("The listener has not yet been stat.initalised!");
		}

		if (hasCompletedJobs(machine)) {
			double sumWaitTimes = stat.sumWaitTimesPerMachine[machine.index()];
			double queueSize = stat.completedJobs[machine.index()].size();

			return sumWaitTimes / queueSize;
		} else {
			return 0.0;
		}
	}

	public double getAverageWaitTimesAllMachines() {
		if (!stat.initialised) {
			throw new RuntimeException("The listener has not yet been stat.initialised!");
		}

		if (stat.sumWaitTimes != 0.0) {
			return stat.sumWaitTimes / stat.numCompleted;
		} else {
			return 0.0;
		}
	}

	@SuppressWarnings("unchecked")
	public void clear() {
		stat.startedJobs = new OperationStartStat[stat.numMachines];
		stat.completedJobs = new Queue[stat.numMachines];
		stat.sumWaitTimesPerMachine = new double[stat.numMachines];

		for (int i = 0; i < stat.numMachines; i++) {
			stat.completedJobs[i] = new LinkedList<OperationCompletionStat>();
			stat.sumWaitTimesPerMachine[i] = 0.0;
		}

		stat.sumWaitTimes = 0.0;
		stat.numCompleted = 0;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		HuntListener obj = (HuntListener) super.clone();

		obj.stat = this.stat;

		return obj;
	}

	private class OperationStartStat {
		PrioRuleTarget entry;

		double arrivalTime;
		double startTime;
	}

	// Used explicitly for cloning, since it will be used by the experiment as well.
	private class ReferenceStat {
		int maxSize;

		OperationStartStat[] startedJobs;
		Queue<OperationCompletionStat>[] completedJobs;
		int numMachines;

		double sumWaitTimes = 0.0;
		double[] sumWaitTimesPerMachine;
		int numCompleted = 0;

		boolean initialised = false;
	}
}
