package app.node.basic;

import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_DIVISION)
public class OpDivision implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_DIVISION;
	private static final double MINIMUM_THRESHOLD_FROM_ZERO = 0.000001;

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
	public int getSize() {
		return leftChild.getSize() + rightChild.getSize() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		double leftEval = leftChild.evaluate(data);
		double rightEval = rightChild.evaluate(data);

		if (Math.abs(rightEval) < MINIMUM_THRESHOLD_FROM_ZERO) {
			return 1.0;
		} else {
			return leftEval / rightEval;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}
		OpDivision other = (OpDivision) o;
		return this.leftChild.equals(other.leftChild) &&
				this.rightChild.equals(other.rightChild);
	}

	@Override
	public String toString() {
		return "(" + NODE_DEFINITION + " " + leftChild.toString() + " " + rightChild.toString() + ")";
	}

}
