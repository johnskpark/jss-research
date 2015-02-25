package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_SLACK)
public class ScoreSlack implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_SLACK;

	public ScoreSlack() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		return Math.max(entry.getDueDate() - entry.getShop().simTime() - entry.remainingProcTime(), 0);
	}

}
