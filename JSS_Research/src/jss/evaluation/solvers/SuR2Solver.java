package jss.evaluation.solvers;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalData;
import jss.evaluation.JSSEvalSolver;
import jss.evaluation.RuleParser;
import jss.evaluation.node.INode;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class SuR2Solver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public SuR2Solver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new SuR2DR());

		setSolver(solver);
	}

	// TODO docs.
	private class SuR2DR implements IActionHandler {

		private String ruleString = "(+ (+ (+ (- (/ (* (+ PR RM) W) PR) (/ (* (+ RJ RM) PR) RT)) RT) (/ (* (* (/ DD (+ PR RM)) RT) W) (* PR PR))) (* (/ RT PR) (+ RJ RM)))";
		private INode node;

		public SuR2DR() {
			RuleParser parser = new RuleParser();
			node = parser.getRuleFromString(ruleString);
		}

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

		private IJob getJobFromRule(IMachine machine, IProblemInstance problem, double time) {
			double bestPriority = Double.POSITIVE_INFINITY;
			IJob bestJob = null;

			for (IJob job : machine.getWaitingJobs()) {
				if (!machine.equals(job.getCurrentMachine())) {
					continue;
				}

				JSSEvalData data = new JSSEvalData(problem, machine, job, time);

				double priority = node.evaluate(data);

				if (priority < bestPriority) {
					bestPriority = priority;
					bestJob = job;
				}
			}

			return bestJob;
		}
	}

}