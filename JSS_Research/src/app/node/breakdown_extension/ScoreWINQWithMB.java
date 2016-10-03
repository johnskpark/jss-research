package app.node.breakdown_extension;

import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_WINQ_WITH_MB)
public class ScoreWINQWithMB extends AbsMBNode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WINQ_WITH_MB;

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
		PrioRuleTarget job = data.getPrioRuleTarget();

		int nextTask = job.getTaskNumber() + 1;
		if (nextTask >= job.numOps()) {
			return 0.0;
		} else {
			WorkStation nextMachine = job.getOps()[nextTask].machine;

			return getWorkInQueue(job, nextMachine);
		}
	}

	private double getWorkInQueue(PrioRuleTarget job, WorkStation nextMachine) {
		PriorityQueue<Job> queue = nextMachine.queue;

		// WINQ = all the jobs in the queue and the time left on the current job.
		double winq = 0.0;
		for (int i = 0; i < queue.size(); i++) {
			winq += getProcTime(queue.get(i), nextMachine);
		}

		IndividualMachine indMachine = nextMachine.machDat()[0];

		if (indMachine.procFinished > job.getShop().simTime()) {
			// Calculate the work remaining on the current job being processed.
			double workRemaining;
			if (indMachine.procFinished <= getDeactivateTime(nextMachine)) {
				workRemaining = indMachine.procFinished - job.getShop().simTime();
			} else {
				workRemaining = indMachine.procFinished - job.getShop().simTime() + getNextRepairTime(nextMachine);
			}
			winq += workRemaining;
		}

		return winq;
	}

	private double getProcTime(PrioRuleTarget job, WorkStation machine) {
		double pt = job.getCurrentOperation().procTime;
		if (job.getShop().simTime() + pt <= getDeactivateTime(machine)) {
			return pt;
		} else {
			return pt + getNextRepairTime(machine);
		}
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
