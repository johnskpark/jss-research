package app.listener.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import app.evolution.AbsWorkStationListener;
import ec.EvolutionState;
import ec.util.Parameter;

public class HuntListener extends AbsWorkStationListener {

	private static final long serialVersionUID = -3624321421753776979L;

	public static final String P_MAX_JOBS = "max-jobs";

	private int maxSize;

	private Map<WorkStation, Queue<OperationCompletionStat>> completedJobs = new HashMap<WorkStation, Queue<OperationCompletionStat>>();
	private Map<WorkStation, OperationStartStat> startedJobs = new HashMap<WorkStation, OperationStartStat>();

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
		assert startedJobs.get(machine) == null;

		OperationStartStat stat = new OperationStartStat();
		stat.entry = machine.justStarted;
		stat.arrivalTime = stat.entry.getArriveTime();
		stat.startTime = stat.entry.getShop().simTime();

		startedJobs.put(machine, stat);
	}

	public void operationComplete(WorkStation machine) {
		assert startedJobs.get(machine).entry == machine.justCompleted;

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
			queue.poll();
		}
		queue.offer(stat);

		startedJobs.put(machine, null);
	}

	public Queue<OperationCompletionStat> getLastCompletedJobs(WorkStation machine) {
		return completedJobs.get(machine);
	}

	public void clear() {
		completedJobs.clear();
	}

	private class OperationStartStat {
		PrioRuleTarget entry;

		double arrivalTime;
		double startTime;
	}

}
