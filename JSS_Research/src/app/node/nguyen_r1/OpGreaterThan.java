package app.node.nguyen_r1;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_GREATER_THAN)
public class OpGreaterThan implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_GREATER_THAN;

	private INode leftChild;
	private INode rightChild;

	public OpGreaterThan(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
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
		double attribute = leftChild.evaluate(data);
		double threshold = rightChild.evaluate(data);

		if (attribute > threshold) {
			return -1.0;
		} else {
			return 1.0;
		}
	}

}
