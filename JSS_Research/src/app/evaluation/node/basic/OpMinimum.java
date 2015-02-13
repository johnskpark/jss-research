package app.evaluation.node.basic;

import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_MINIMUM)
public class OpMinimum implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MINIMUM;

	private INode leftChild;
	private INode rightChild;

	/**
	 * TODO javadoc.
	 * @param leftChild
	 * @param rightChild
	 */
	public OpMinimum(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		double leftEval = leftChild.evaluate(data);
		double rightEval = rightChild.evaluate(data);

		return Math.min(leftEval, rightEval);
	}

}
