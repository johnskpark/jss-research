package app.listener.hunt;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class HuntListener implements NotifierListener<WorkStation, WorkStationEvent> {

	private int maxSize;

	private Map<WorkStation, Queue<OperationCompletionStats>> completedJobs = new HashMap<WorkStation, Queue<OperationCompletionStats>>();
	private Map<WorkStation, OperationStartStats> startedJobs = new HashMap<WorkStation, OperationStartStats>();

	public HuntListener(int max) {
		maxSize = max;
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// TODO this needs to be
		if (event != WorkStation.WS_JOB_COMPLETED || event != WorkStation.WS_JOB_SELECTED) {
			return;
		}

		if (!completedJobs.containsKey(notifier)) {
			completedJobs.put(notifier, new LinkedList<OperationCompletionStats>());
		}
		Queue<OperationCompletionStats> data = completedJobs.get(notifier);

		if (data.size() == maxSize) {
			data.poll();
		}
		PrioRuleTarget entry = notifier.justCompleted;

		// TODO Hmm... It seems that the workstation doesn't actually have the
		// processing start time available for information...
		OperationCompletionStats stats = new OperationCompletionStats(entry,
				entry.getArriveTime(), // Arrival time
				0, // Start time
				0, // Wait time
				entry.getShop().simTime()); // Completion time



		data.offer(stats);
	}

	public Queue<OperationCompletionStats> getLastCompletedJobs(WorkStation machine) {
		return completedJobs.get(machine);
	}

	public void clear() {
		completedJobs.clear();
	}

	private class OperationStartStats {
		PrioRuleTarget entry;

		double arrivalTime;
		double startTime;
	}

}
