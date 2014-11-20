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
public class FIFOSolver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public FIFOSolver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new FIFODR());

		setSolver(solver);
	}

	// TODO docs.
	private class FIFODR implements IActionHandler {

		@Override
		public Action getAction(IMachine machine, IProblemInstance problem, double time) {
			List<IJob> jobs = problem.getJobs();

			double earliestCompletion = Double.POSITIVE_INFINITY;

			for (IJob job : jobs) {
				if (!machine.equals(job.getNextMachine())) {
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

			for (IJob job : jobs) {
				if (!machine.equals(job.getNextMachine()) ||
						job.getReadyTime() >= earliestCompletion) {
					continue;
				}

				IMachine lastMachine = job.getLastMachine();
				double priority = (lastMachine != null) ?
						lastMachine.getReadyTime() : job.getReadyTime();

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
