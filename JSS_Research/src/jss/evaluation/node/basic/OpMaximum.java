package jss.evaluation.node.basic;

import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.OP_MAXIMUM)
public class OpMaximum implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MAXIMUM;

	private INode leftChild;
	private INode rightChild;

	/**
	 * TODO javadoc.
	 * @param leftChild
	 * @param rightChild
	 */
	public OpMaximum(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		double leftEval = leftChild.evaluate(data);
		double rightEval = rightChild.evaluate(data);

		return Math.max(leftEval, rightEval);
	}

}
