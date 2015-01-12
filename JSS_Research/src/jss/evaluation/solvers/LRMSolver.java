package jss.evaluation.solvers;

import java.util.List;

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
public class LRMSolver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public LRMSolver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new LRMDR());

		setSolver(solver);
	}

	// TODO docs.
	private class LRMDR implements IActionHandler {

		@Override
		public Action getAction(IMachine machine, IProblemInstance problem, double time) {
			List<IJob> jobs = problem.getJobs();

			double bestPriority = Double.NEGATIVE_INFINITY;
			IJob bestJob = null;

			for (IJob job : jobs) {
				if (!machine.equals(job.getCurrentMachine())) {
					continue;
				}

				double priority = Double.NEGATIVE_INFINITY;

				if (priority > bestPriority) {
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
