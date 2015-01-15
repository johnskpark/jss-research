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
@NodeAnnotation(node=NodeDefinition.SCORE_CURRENT_TIME)
public class ScoreCurrentTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_CURRENT_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreCurrentTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		return data.getTime();
	}

}
