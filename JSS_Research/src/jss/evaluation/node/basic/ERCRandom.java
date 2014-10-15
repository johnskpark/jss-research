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
@NodeAnnotation(node=NodeDefinition.ERC_RANDOM)
public class ERCRandom implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ERC_RANDOM;

	private double value;

	/**
	 * TODO javadoc.
	 * @param value
	 */
	public ERCRandom(double value) {
		this.value = value;
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		return value;
	}

}
