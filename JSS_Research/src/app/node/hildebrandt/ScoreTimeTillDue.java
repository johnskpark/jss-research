package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
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
	public double evaluate(PrioRuleTarget entry) {
		return Math.max(entry.getDueDate() - entry.getShop().simTime(), 0);
	}

}
