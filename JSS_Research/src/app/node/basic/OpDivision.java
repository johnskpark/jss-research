package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_DIVISION)
public class OpDivision implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_DIVISION;

	private INode leftChild;
	private INode rightChild;

	public OpDivision(INode leftChild, INode rightChild) {
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

		if (rightEval == 0) {
			return leftEval;
		} else {
			return leftEval / rightEval;
		}
	}

}
