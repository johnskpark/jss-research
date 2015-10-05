package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
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
	public double evaluate(NodeData data) {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		return this.value == ((ERCRandom) o).value;
	}

}
