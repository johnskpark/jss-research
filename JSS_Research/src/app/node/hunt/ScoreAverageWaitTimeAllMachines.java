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

//The average wait time of last five jobs processed all machines on the shop floor.
@NodeAnnotation(node=NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE)
public class ScoreAverageWaitTimeAllMachines implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE;

	public ScoreAverageWaitTimeAllMachines() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		PrioRuleTarget entry = data.getEntry();
		HuntListener listener = (HuntListener) data.getWorkStationListener();

		WorkStation[] machines = entry.getShop().getMachines();

		double averageWaitTime = 0.0;
		for (WorkStation machine : machines) {
			Queue<OperationCompletionStat> completedJobsQueue = listener.getLastCompletedJobs(machine);
			if (completedJobsQueue == null) {
				continue;
			}

			double machineWaitTime = 0.0;

			for (OperationCompletionStat stat : completedJobsQueue) {
				machineWaitTime += stat.getWaitTime();
			}

			averageWaitTime += machineWaitTime / completedJobsQueue.size();
		}
		averageWaitTime /= machines.length;

		return averageWaitTime;
	}

}
