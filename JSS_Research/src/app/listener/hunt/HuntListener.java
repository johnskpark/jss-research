package app.listener.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import app.listener.AbsWorkStationListener;
import ec.EvolutionState;
import ec.util.Parameter;

public class HuntListener extends AbsWorkStationListener {

	private static final long serialVersionUID = -3624321421753776979L;

	public static final String P_MAX_JOBS = "max-jobs";

	private int maxSize;

	private Map<WorkStation, OperationStartStat> startedJobs = new HashMap<WorkStation, OperationStartStat>();

	private Map<WorkStation, Queue<OperationCompletionStat>> completedJobs = new HashMap<WorkStation, Queue<OperationCompletionStat>>();

	private double sumWaitTimes = 0.0;
	private int numCompleted = 0;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			maxSize = state.parameters.getInt(base.push(P_MAX_JOBS), null);
		} catch (NumberFormatException ex) {
			state.output.fatal("Maximum number of job size not set for HuntListener.");
		}
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_JOB_SELECTED) {
			operationStart(notifier);
		} else if (event == WorkStation.WS_JOB_COMPLETED){
			operationComplete(notifier);
		}
	}

	public void operationStart(WorkStation machine) {
		if (startedJobs.get(machine) != null) {
			throw new RuntimeException("The machine should not currently be processing any jobs");
		}

		OperationStartStat stat = new OperationStartStat();
		stat.entry = machine.justStarted;
		stat.arrivalTime = stat.entry.getArriveTime();
		stat.startTime = stat.entry.getShop().simTime();

		startedJobs.put(machine, stat);
	}

	public void operationComplete(WorkStation machine) {
		if (startedJobs.get(machine).entry != machine.justCompleted) {
			throw new RuntimeException("The job selected to be processed on the machine does not match with the completed job");
		}

		if (!completedJobs.containsKey(machine)) {
			completedJobs.put(machine, new LinkedList<OperationCompletionStat>());
		}

		OperationStartStat startStat = startedJobs.get(machine);

		OperationCompletionStat stat = new OperationCompletionStat(startStat.entry);
		stat.setArrivalTime(startStat.arrivalTime);
		stat.setStartTime(startStat.startTime);
		stat.setWaitTime(startStat.startTime - startStat.arrivalTime);
		stat.setCompletionTime(startStat.entry.getShop().simTime());

		Queue<OperationCompletionStat> queue = completedJobs.get(machine);
		if (queue.size() == maxSize) {
			OperationCompletionStat oldStat = queue.poll();

			sumWaitTimes -= oldStat.getWaitTime();
			numCompleted--;
		}

		queue.offer(stat);

		sumWaitTimes += stat.getWaitTime();
		numCompleted++;

		startedJobs.put(machine, null);
	}

	public Queue<OperationCompletionStat> getLastCompletedJobs(WorkStation machine) {
		return completedJobs.get(machine);
	}

	public double getAverageWaitTimesAllMachines() {
		return (sumWaitTimes != 0.0) ? (sumWaitTimes / numCompleted) : 0.0;
	}

	public void clear() {
		completedJobs.clear();
		startedJobs.clear();
	}

	private class OperationStartStat {
		PrioRuleTarget entry;

		double arrivalTime;
		double startTime;
	}

}
