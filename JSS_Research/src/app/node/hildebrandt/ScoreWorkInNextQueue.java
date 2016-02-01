package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE)
public class ScoreWorkInNextQueue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE;

	public ScoreWorkInNextQueue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		PrioRuleTarget entry = data.getEntry();

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		}
		WorkStation nextMachine = entry.getOps()[nextTask].machine;

		return nextMachine.workContent(false);
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
