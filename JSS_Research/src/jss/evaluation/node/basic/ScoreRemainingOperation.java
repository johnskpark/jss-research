package jss.evaluation.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_REMAINING_OPERATION)
public class ScoreRemainingOperation implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_REMAINING_TIME;

	/**
	 * TODO javadoc.
	 */
	public ScoreRemainingOperation() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IProblemInstance problem = data.getProblem();
		IJob job = data.getJob();

		int remainingOperation = 0;
		for (IMachine machine : problem.getMachines()) {
			if (job.isProcessable(machine)) {
				remainingOperation++;
			}
		}

		return remainingOperation;
	}

}
