package jss.evaluation.node.hildebrandt;

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
@NodeAnnotation(node=NodeDefinition.SCORE_NEXT_PROCESSING_TIME)
public class ScoreNextProcessingTime implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NEXT_PROCESSING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreNextProcessingTime() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IJob job = data.getJob();
		IMachine machine = job.getNextMachine();

		return job.getProcessingTime(machine);
	}

}
