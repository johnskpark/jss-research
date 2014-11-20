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
@NodeAnnotation(node=NodeDefinition.SCORE_JOB_READY_TIME)
public class ScoreJobReadyTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_JOB_READY_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreJobReadyTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IJob job = data.getJob();
		return job.getReadyTime();
	}

}
