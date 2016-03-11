package app.node.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_NUMBER_WAITING_NEXT_MACHINE)
public class ScoreNumberWaitingNextMachine implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NUMBER_WAITING_NEXT_MACHINE;

	public ScoreNumberWaitingNextMachine() {
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

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		} else {
			WorkStation machine = entry.getOps()[nextTask].machine;
			return machine.numJobsWaiting();
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
