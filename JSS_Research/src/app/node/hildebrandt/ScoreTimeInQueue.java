package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_TIME_IN_QUEUE)
public class ScoreTimeInQueue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_TIME_IN_QUEUE;

	public ScoreTimeInQueue() {
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

		return Math.max(entry.getShop().simTime() - entry.getArriveTime(), 0);
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
