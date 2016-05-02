package app.node.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

import java.util.Map;
import java.util.Queue;

import app.IWorkStationListener;
import app.listener.hunt.HuntListener;
import app.listener.hunt.OperationCompletionStat;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

// The average wait time of last five jobs processed at the next machine job visits.
@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE)
public class ScoreAverageWaitTimeNextMachine implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_WAIT_TIME_NEXT_MACHINE;

	public ScoreAverageWaitTimeNextMachine() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		PrioRuleTarget entry = data.getEntry();

		Map<String, IWorkStationListener> listeners = data.getWorkStationListener();
		HuntListener listener = (HuntListener) listeners.get(HuntListener.class.getSimpleName());

		double averageWaitTime = 0.0;

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask < entry.numOps()) {
			WorkStation machine = entry.getOps()[nextTask].machine;

			if (listener.hasCompletedJobs(machine)) {
				Queue<OperationCompletionStat> completedJobsQueue = listener.getLastCompletedJobs(machine);

				for (OperationCompletionStat stat : completedJobsQueue) {
					averageWaitTime += stat.getWaitTime();
				}
				averageWaitTime /= completedJobsQueue.size();

				return averageWaitTime;
			}
		}

		return averageWaitTime;
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
