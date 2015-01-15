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
@NodeAnnotation(node=NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE)
public class ScoreWorkInNextQueue implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE;

	/**
	 * TODO javadoc.
	 */
	public ScoreWorkInNextQueue() {
	}

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public double evaluate(JSSEvalData data) {
		IJob job = data.getJob();
		IMachine machine = job.getNextMachine();

		double priority = 0;
		if (machine != null) {
			for (IJob waitingJob : machine.getWaitingJobs()) {
				priority += waitingJob.getProcessingTime(machine);
			}

			IJob currentJob;
			if ((currentJob = machine.getCurrentJob()) != null) {
				priority += currentJob.getProcessingTime(machine);
			}
		}

		return priority;
	}

}
