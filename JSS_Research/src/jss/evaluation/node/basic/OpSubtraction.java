package jss.evaluation.node.basic;

import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.OP_SUBTRACTION)
public class OpSubtraction implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_SUBTRACTION;

	private INode leftChild;
	private INode rightChild;

	/**
	 * TODO javadoc.
	 * @param leftChild
	 * @param rightChild
	 */
	public OpSubtraction(INode leftChild, INode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		return leftChild.evaluate(data) - rightChild.evaluate(data);
	}

}
