package app.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_OPERATIONAL_DUE_DATE)
public class ScoreOperationalDueDate implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_OPERATIONAL_DUE_DATE;

	public ScoreOperationalDueDate() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		return entry.getCurrentOperationDueDate();
	}

}
