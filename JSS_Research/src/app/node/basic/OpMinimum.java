package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_MINIMUM)
public class OpMinimum implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MINIMUM;

	private INode leftChild;
	private INode rightChild;

	public OpMinimum(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(NodeData data) {
		double leftEval = leftChild.evaluate(data);
		double rightEval = rightChild.evaluate(data);

		return Math.min(leftEval, rightEval);
	}

}
