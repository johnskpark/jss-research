package app.node.breakdown_current;

import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

@NodeAnnotation(node=NodeDefinition.SCORE_NEXT_MACHINE_MIN_WAIT_TIME)
public class ScoreNextMachineMinWaitTime extends AbsMBNode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NEXT_MACHINE_MIN_WAIT_TIME;

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
			double t = job.getShop().simTime();

			Operation nextOp = job.getOps()[nextTask];
			WorkStation nextMachine = nextOp.machine;

			double minWaitTime;

			// If the machine is currently processing a job, then its the expected completion time of the job.
			// Otherwise, check if the next machine is currently broken down, and then use the time when the
			// machine comes back online.
			if (nextMachine.machDat()[0].curJob != null) {
				minWaitTime = nextMachine.machDat()[0].procFinished - t;
			} else if (t <= getActivateTime(nextMachine) && t >= getDeactivateTime(nextMachine)) {
				minWaitTime = getActivateTime(nextMachine) - t;
			} else {
				minWaitTime = 0.0;
			}

			return minWaitTime;
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
