package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_ADDITION)
public class OpAddition implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_ADDITION;

	private INode leftChild;
	private INode rightChild;

	public OpAddition(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		return leftChild.evaluate(data) + rightChild.evaluate(data);
	}

}
