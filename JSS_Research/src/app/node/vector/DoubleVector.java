package app.node.vector;

import java.util.List;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.DOUBLE_VECTOR)
public class DoubleVector implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.DOUBLE_VECTOR;

	private List<Double> values;

	public DoubleVector(List<Double> values) {
		this.values = values;
	}

	public double getValue(int index) {
		return values.get(index);
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return values.size();
	}

	@Override
	public double evaluate(NodeData data) {
		throw new UnsupportedOperationException("evaluate() should not directly be called on a double vector.");
	}
}
