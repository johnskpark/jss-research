package app.evaluation.node.basic;

import app.evaluation.JasimaEvalData;
import app.evaluation.node.INode;
import app.evaluation.node.NodeAnnotation;
import app.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.OP_DIVISION)
public class OpDivision implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_DIVISION;

	private INode leftChild;
	private INode rightChild;

	/**
	 * TODO javadoc.
	 * @param leftChild
	 * @param rightChild
	 */
	public OpDivision(INode leftChild, INode rightChild) {
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

		if (rightEval == 0) {
			return leftEval;
		} else {
			return leftEval / rightEval;
		}
	}

}
