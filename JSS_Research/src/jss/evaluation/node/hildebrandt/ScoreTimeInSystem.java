package jss.evaluation.node.hildebrandt;

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
@NodeAnnotation(node=NodeDefinition.SCORE_TIME_IN_SYSTEM)
public class ScoreTimeInSystem implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_TIME_IN_SYSTEM;

	/**
	 * TODO javadoc.
	 */
	public ScoreTimeInSystem() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IJob job = data.getJob();
		double time = data.getTime();

		return time - job.getReadyTime();
	}

}
