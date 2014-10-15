package jss.evaluation.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_PROCESSING_TIME)
public class ScoreProcessingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_PROCESSING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreProcessingTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IMachine machine = data.getMachine();
		IJob job = data.getJob();
		return job.getProcessingTime(machine);
	}

}
