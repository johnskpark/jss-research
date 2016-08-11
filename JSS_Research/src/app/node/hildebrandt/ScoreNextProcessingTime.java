package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_NEXT_PROCESSING_TIME)
public class ScoreNextProcessingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NEXT_PROCESSING_TIME;

	public ScoreNextProcessingTime() {
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
		PrioRuleTarget entry = data.getPrioRuleTarget();

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		}

		return entry.getOps()[nextTask].procTime;
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
