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
@NodeAnnotation(node=NodeDefinition.OP_MULTIPLICATION)
public class OpMultiplication implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MULTIPLICATION;

	private INode leftChild;
	private INode rightChild;

	/**
	 * TODO javadoc.
	 * @param leftChild
	 * @param rightChild
	 */
	public OpMultiplication(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		return leftChild.evaluate(data) * rightChild.evaluate(data);
	}

}
