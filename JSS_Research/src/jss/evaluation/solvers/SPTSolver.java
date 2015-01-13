package jss.evaluation.solvers;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalSolver;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class SPTSolver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public SPTSolver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new SPTDR());

		setSolver(solver);
	}

	// TODO docs.
	private class SPTDR implements IActionHandler {

		@Override
		public Action getAction(IMachine machine, IProblemInstance problem, double time) {
			double earliestCompletion = Double.POSITIVE_INFINITY;

			for (IJob job : machine.getWaitingJobs()) {
				if (!machine.equals(job.getCurrentMachine())) {
					continue;
				}

				double completion = Math.max(machine.getReadyTime(),
						job.getReadyTime()) +
						job.getSetupTime(machine) +
						job.getProcessingTime(machine);

				earliestCompletion = Math.min(earliestCompletion, completion);
			}

			double bestPriority = Double.POSITIVE_INFINITY;
			IJob bestJob = null;

			for (IJob job : machine.getWaitingJobs()) {
				if (!machine.equals(job.getCurrentMachine())) {
					continue;
				}

				double priority = job.getProcessingTime(machine);

				if (priority < bestPriority) {
					bestPriority = priority;
					bestJob = job;
				}
			}

			if (bestJob != null) {
				// Simply process the job as early as possible.
				double t = Math.max(machine.getReadyTime(), bestJob.getReadyTime());
				return new Action(machine, bestJob, t);
			} else {
				return null;
			}
		}

	}

}
