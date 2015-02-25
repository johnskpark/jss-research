package app.node.basic;

import jasima.shopSim.core.PrioRuleTarget;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.ERC_RANDOM)
public class ERCRandom implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ERC_RANDOM;

	private double value;

	public ERCRandom(double value) {
		this.value = value;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(PrioRuleTarget entry) {
		return value;
	}

}
