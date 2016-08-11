package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_TIME_TILL_DUE)
public class ScoreTimeTillDue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_TIME_TILL_DUE;

	public ScoreTimeTillDue() {
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

		return Math.max(entry.getDueDate() - entry.getShop().simTime(), 0);
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
