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
@NodeAnnotation(node=NodeDefinition.SCORE_LARGE_VALUE)
public class ScoreLargeValue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_LARGE_VALUE;

	private static final double LARGE_VALUE = 100000000.0;

	/**
	 * TODO javadoc.
	 */
	public ScoreLargeValue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JasimaEvalData data) {
		return LARGE_VALUE;
	}

}
