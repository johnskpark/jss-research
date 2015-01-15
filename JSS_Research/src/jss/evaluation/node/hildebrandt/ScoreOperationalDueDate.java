package jss.evaluation.node.hildebrandt;

import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.node.NodeDefinition;
import jss.problem.dynamic_problem.DynamicJob;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
@NodeAnnotation(node=NodeDefinition.SCORE_OPERATIONAL_DUE_DATE)
public class ScoreOperationalDueDate implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_OPERATIONAL_DUE_DATE;

	/**
	 * TODO javadoc.
	 */
	public ScoreOperationalDueDate() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		DynamicJob job = (DynamicJob) data.getJob(); // Need the flow factor.

		double priority = job.getReadyTime() + job.getFlowFactor() * job.getProcessingTime(0);
		for (int i = 1; i < job.getNumOperations(); i++) {
			priority += job.getFlowFactor() * job.getProcessingTime(i);
		}

		return priority;
	}

}
