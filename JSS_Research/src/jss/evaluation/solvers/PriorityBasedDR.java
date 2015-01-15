package jss.evaluation.solvers;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class PriorityBasedDR implements IActionHandler {

	private INode node;

	/**
	 * TODO javadoc.
	 * @param node
	 */
	public PriorityBasedDR(INode node) {
		this.node = node;
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem, double time) {
		IJob job;
		if (machine.getWaitingJobs().isEmpty()) {
			return null;
		} else if (machine.getWaitingJobs().size() == 1) {
			job = machine.getWaitingJobs().get(0);
		} else {
			job = getJobFromPriorities(machine, problem, time);
		}

		// Simply process the job as early as possible.
		double t = Math.max(machine.getReadyTime(), job.getReadyTime());
		return new Action(machine, job, t);
	}

	private IJob getJobFromPriorities(IMachine machine, IProblemInstance problem, double time) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : machine.getWaitingJobs()) {
			if (!machine.equals(job.getCurrentMachine())) {
				continue;
			}

			JSSEvalData data = new JSSEvalData(problem, machine, job, time);
			double priority = node.evaluate(data);

			if (priority > bestPriority) {
				bestPriority = priority;
				bestJob = job;
			}
		}

		return bestJob;
	}
}
