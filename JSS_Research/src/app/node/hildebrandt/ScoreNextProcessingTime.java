package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
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
	public double evaluate(PrioRuleTarget entry) {
		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			return 0;
		}

		return entry.getOps()[nextTask].procTime;
	}

}
