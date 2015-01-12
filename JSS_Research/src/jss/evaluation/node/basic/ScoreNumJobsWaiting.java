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
@NodeAnnotation(node=NodeDefinition.SCORE_NUM_JOBS_WAITING)
public class ScoreNumJobsWaiting implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NUM_JOBS_WAITING;

	/**
	 * TODO javadoc.
	 */
	public ScoreNumJobsWaiting() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IMachine machine = data.getMachine();

		int numJobs = 0;
		for (IJob job : data.getProblem().getJobs()) {
			if (machine.equals(job.getCurrentMachine())) {
				numJobs++;
			}
		}

		return numJobs;
	}
}
