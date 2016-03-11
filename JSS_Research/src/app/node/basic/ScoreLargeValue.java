package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
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
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		return LARGE_VALUE;
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

}
