package jss.evaluation.solvers;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalConfiguration;
import jss.evaluation.JSSEvalData;
import jss.evaluation.JSSEvalSolver;
import jss.evaluation.node.INode;
import jss.evaluation.node.basic.ERCRandom;
import jss.evaluation.node.basic.OpDivision;
import jss.evaluation.node.basic.OpSubtraction;
import jss.evaluation.node.basic.ScorePenalty;
import jss.evaluation.node.basic.ScoreProcessingTime;
import jss.evaluation.node.basic.ScoreRemainingTime;
import jss.problem.CompletelyReactiveSolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class SuR1Solver extends JSSEvalSolver {

	/**
	 * TODO javadoc.
	 */
	public SuR1Solver() {
		super();
	}

	@Override
	protected void setChildConfiguration(JSSEvalConfiguration config) {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new SuR1DR());

		setSolver(solver);
	}

	private class SuR1DR implements IActionHandler {

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
			double t = Math.max(machine.getReadyTime(), job.getCurrentMachine().getReadyTime());
			return new Action(machine, job, t);

		}

		private IJob getJobFromRule(IMachine machine, IProblemInstance problem, double time) {
			IMachine critMachine = getCriticalMachine(problem);
			IMachine bottleMachine = getBottleMachine(problem);

			double cmi = getCmi(critMachine, problem);
			double cwr = getCwr(machine, critMachine, bottleMachine, problem);
			double dj = getDj(bottleMachine, problem);

			double bestPriority = Double.POSITIVE_INFINITY;
			IJob bestJob = null;

			double earliestCompletionTime = Double.POSITIVE_INFINITY;
			double earliestReadyTime = Double.POSITIVE_INFINITY;

			for (IJob job : problem.getJobs()) {
				if (!job.isProcessable(machine)) {
					continue;
				}

				double readyTime;
				if (job.getProcessingMachine() == null && machine.equals(job.getCurrentMachine())) {
					readyTime = machine.getReadyTime();
				} else if (job.getProcessingMachine() != null && machine.equals(job.getNextMachine())) {
					readyTime = job.getProcessingMachine().getReadyTime();
				} else {
					continue;
				}

				double completionTime = readyTime + job.getProcessingTime(machine);

				earliestReadyTime = Math.min(earliestReadyTime, readyTime);
				earliestCompletionTime = Math.min(earliestCompletionTime, completionTime);
			}

			DTDispatchingRule rule = getDispatchingRule(cmi, cwr, dj);

			for (IJob job : problem.getJobs()) {
				if (!job.isProcessable(machine) ||
						!(job.getProcessingMachine() != null && machine.equals(job.getCurrentMachine()) ||
						job.getProcessingMachine() != null && machine.equals(job.getNextMachine()))) {
					continue;
				}

				double readyTime = job.getCurrentMachine().getReadyTime();

				if (readyTime <= earliestReadyTime + rule.getAlpha() * (earliestCompletionTime - earliestReadyTime)) {
					JSSEvalData data = new JSSEvalData(problem, machine, job, time);

					double priority = rule.getNode().evaluate(data);
					if (priority > bestPriority) {
						bestPriority = priority;
						bestJob = job;
					}
				}
			}

			return bestJob;
		}

		private IMachine getCriticalMachine(IProblemInstance problem) {
			double critWorkloadLeft = Double.NEGATIVE_INFINITY;
			IMachine critMachine = null;

			for (IMachine machine : problem.getMachines()) {
				double workloadLeft = 0.0;

				for (IJob job : problem.getJobs()) {
					workloadLeft += job.isProcessable(machine) ? job.getProcessingTime(machine) : 0.0;
				}

				if (workloadLeft > critWorkloadLeft) {
					critWorkloadLeft = workloadLeft;
					critMachine = machine;
				}
			}

			return critMachine;
		}

		private IMachine getBottleMachine(IProblemInstance problem) {
			double bottleTotalWorkload = Double.NEGATIVE_INFINITY;
			IMachine bottleMachine = null;

			for (IMachine machine : problem.getMachines()) {
				double totalWorkload = 0.0;

				for (IJob job : problem.getJobs()) {
					totalWorkload += job.getProcessingTime(machine);
				}

				if (totalWorkload > bottleTotalWorkload) {
					bottleTotalWorkload = totalWorkload;
					bottleMachine = machine;
				}
			}

			return bottleMachine;
		}

		private double getCmi(IMachine critMachine, IProblemInstance problem) {
			double totalWorkload = 0.0;
			double workloadLeft = 0.0;

			for (IJob job : problem.getJobs()) {
				totalWorkload += job.getProcessingTime(critMachine);
				workloadLeft += job.isProcessable(critMachine) ? job.getProcessingTime(critMachine) : 0.0;
			}

			return totalWorkload / workloadLeft;
		}

		private double getCwr(IMachine curMachine, IMachine critMachine, IMachine bottleMachine, IProblemInstance problem) {
			double totalWorkload = 0.0;
			double critWorkload = 0.0;

			for (IJob job : problem.getJobs()) {
				totalWorkload += job.getProcessingTime(bottleMachine);
				critWorkload += (job.isProcessable(critMachine) && curMachine.equals(critMachine)) ? job.getProcessingTime(bottleMachine) : 0.0;
			}

			return critWorkload / totalWorkload;

		}

		private double getDj(IMachine bottleMachine, IProblemInstance problem) {
			double minProcessing = Double.POSITIVE_INFINITY;
			double maxProcessing = Double.NEGATIVE_INFINITY;

			for (IJob job : problem.getJobs()) {
				minProcessing = Math.min(minProcessing, job.getProcessingTime(bottleMachine));
				maxProcessing = Math.max(maxProcessing, job.getProcessingTime(bottleMachine));
			}

			return minProcessing / maxProcessing;
		}

		private DTDispatchingRule getDispatchingRule(double cmi, double cwr, double dj) {
			DTDispatchingRule rule;

			if (cmi > 0.1) {
				if (cwr > 0.2) {
					if (cwr > 0.8) {
						rule = new DTDispatchingRule(0.131, new ScoreRemainingTime());
					} else if (dj <= 0.3) {
						rule = new DTDispatchingRule(0.198, new ScoreRemainingTime());
					} else {
						rule = new DTDispatchingRule(0.102, new ScoreRemainingTime());
					}
				} else if (cwr > 10) {
					rule = new DTDispatchingRule(0.102, new ScoreRemainingTime());
				} else {
					rule = new DTDispatchingRule(0.131, new ScoreRemainingTime());
				}
			} else if (cwr > 0.1) {
				if (cwr > 0.8) {
					rule = new DTDispatchingRule(0.014, new OpSubtraction(new ERCRandom(0), new OpDivision(new ScorePenalty(), new ScoreProcessingTime())));
				} else if (dj <= 0.3) {
					rule = new DTDispatchingRule(0.198, new OpSubtraction(new ERCRandom(0), new ScoreProcessingTime()));
				} else {
					rule = new DTDispatchingRule(0.131, new ScoreRemainingTime());
				}
			} else if (cwr > 0.8) {
				rule = new DTDispatchingRule(0.830, new ScoreProcessingTime());
			} else if (dj <= 0.2) {
				rule = new DTDispatchingRule(0.198, new OpSubtraction(new ERCRandom(0), new ScoreProcessingTime()));
			} else {
				rule = new DTDispatchingRule(0.102, new ScoreRemainingTime());
			}

			return rule;
		}
	}

	private class DTDispatchingRule {
		private double alpha;
		private INode node;

		public DTDispatchingRule(double alpha, INode node) {
			this.alpha = alpha;
			this.node = node;
		}

		public double getAlpha() {
			return alpha;
		}

		public INode getNode() {
			return node;
		}
	}

}
