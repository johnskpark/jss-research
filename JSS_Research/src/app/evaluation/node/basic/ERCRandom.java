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
	public double evaluate(JasimaEvalData data) {
		return value;
	}

}
