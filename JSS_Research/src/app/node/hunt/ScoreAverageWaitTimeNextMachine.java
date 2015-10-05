package app.node.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

import java.util.Queue;

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
	public double evaluate(NodeData data) {
		PrioRuleTarget entry = data.getEntry();
		HuntListener listener = (HuntListener) data.getWorkStationListener();

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;

			Queue<OperationCompletionStat> completedJobsQueue = listener.getLastCompletedJobs(machine);
			if (completedJobsQueue == null) {
				return 0;
			} else {
				double averageWaitTime = 0.0;
				for (OperationCompletionStat stat : completedJobsQueue) {
					averageWaitTime += stat.getWaitTime();
				}
				averageWaitTime /= completedJobsQueue.size();

				return averageWaitTime;
			}
		}
	}

}
