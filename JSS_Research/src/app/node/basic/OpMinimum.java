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
	public int getSize() {
		return leftChild.getSize() + rightChild.getSize() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		double leftEval = leftChild.evaluate(data);
		double rightEval = rightChild.evaluate(data);

		return Math.min(leftEval, rightEval);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		OpMinimum other = (OpMinimum) o;
		return this.leftChild.equals(other.leftChild) &&
				this.rightChild.equals(other.rightChild);
	}

	@Override
	public String toString() {
		return "(" + NODE_DEFINITION + " " + leftChild.toString() + " " + rightChild.toString() + ")";
	}

}
