package jss.evaluation.solvers;

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
public class CoopTwoRuleDR implements IActionHandler {

	private INode rule1;
	private INode rule2;

	/**
	 * TODO javadoc.
	 * @param rules
	 */
	public CoopTwoRuleDR(List<INode> rules) {
		this.rule1 = rules.get(0);
		this.rule2 = rules.get(1);
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem, double time) {
		if (selectFirstRule(machine, problem, time)) {
			return getAction(rule1, machine, problem);
		} else {
			return getAction(rule2, machine, problem);
		}
	}

	// Determine whether to use the first rule out of the two rules.
	private boolean selectFirstRule(IMachine machine, IProblemInstance problem, double time) {
		return time == 0;
	}

	// Get the action using the specified rule
	private Action getAction(INode rule, IMachine machine, IProblemInstance problem) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : machine.getWaitingJobs()) {
			if (!machine.equals(job.getCurrentMachine())) {
				continue;
			}

			JSSEvalData data = new JSSEvalData(problem, machine, job);
			double priority = rule.evaluate(data);

			if (priority > bestPriority) {
				bestPriority = priority;
				bestJob = job;
			}
		}

		if (bestJob != null) {
			// Simply process the job as early as possible.
			double time = Math.max(machine.getReadyTime(), bestJob.getReadyTime());
			return new Action(machine, bestJob, time);
		} else {
			return null;
		}
	}

}
