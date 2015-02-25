package app.node.basic;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.SCORE_LARGE_VALUE)
public class ScoreLargeValue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_LARGE_VALUE;

	private static final double LARGE_VALUE = 100000000.0;

	public ScoreLargeValue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		return LARGE_VALUE;
	}

}
