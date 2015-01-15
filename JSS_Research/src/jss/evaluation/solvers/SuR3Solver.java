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
public class SuR3Solver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public SuR3Solver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new SuR3DR());

		setSolver(solver);
	}

	// TODO docs.
	private class SuR3DR implements IActionHandler {

		@Override
		public Action getAction(IMachine machine, IProblemInstance problem, double time) {
			IJob job;
			if (machine.getWaitingJobs().isEmpty()) {
				return null;
			} else if (machine.getWaitingJobs().size() == 1) {
				job = machine.getWaitingJobs().get(0);
			} else {
				job = getJobFromRule(machine, problem, time);
			}

			// Simply process the job as early as possible.
			double t = Math.max(machine.getReadyTime(), job.getReadyTime());
			return new Action(machine, job, t);

		}

		// TODO not yet implemented
		private IJob getJobFromRule(IMachine machine, IProblemInstance problem, double time) {
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

			return bestJob;
		}
	}

}
