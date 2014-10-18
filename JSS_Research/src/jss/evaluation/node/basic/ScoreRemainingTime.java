package jss.evaluation.node.basic;

import jss.IJob;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_REMAINING_TIME)
public class ScoreRemainingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_REMAINING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreRemainingTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IJob job = data.getJob();

		return job.getRemainingTime();
	}

}
