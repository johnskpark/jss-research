package jss.evaluation.sample;

import java.util.List;

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
	public Action getAction(IMachine machine, IProblemInstance problem) {
		List<IJob> jobs = problem.getJobs();

		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : jobs) {
			if (!machine.equals(job.getNextMachine())) {
				continue;
			}

			JSSEvalData data = new JSSEvalData(problem, machine, job);
			double priority = node.evaluate(data);

			if (priority > bestPriority) {
				bestPriority = priority;
				bestJob = job;
			}
		}

		if (bestJob != null) {
			// Simply process the job as early as possible.
			double time = Math.max(machine.getTimeAvailable(), bestJob.getReadyTime(machine));
			return new Action(machine, bestJob, time);
		} else {
			return null;
		}
	}
}
