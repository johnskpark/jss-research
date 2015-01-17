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

		private double cwrThreshold = 0.9;

		private double alpha1 = 0.069;
		private String ruleString1 = "(/ (* (+ RJ 0.594845) (+ RT PR)) (+ W PR))";
		private DTDispatchingRule rule1;

		private double alpha2 = 0.128;
		private String ruleString2 = "(/ (- RT PR) (+ W PR))";
		private DTDispatchingRule rule2;

		public SuR3DR() {
			RuleParser parser = new RuleParser();

			rule1 = new DTDispatchingRule(alpha1, parser.getRuleFromString(ruleString1));
			rule2 = new DTDispatchingRule(alpha2, parser.getRuleFromString(ruleString2));
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
			double t = Math.max(machine.getReadyTime(), job.getCurrentMachine().getReadyTime());
			return new Action(machine, job, t);

		}

		private IJob getJobFromRule(IMachine machine, IProblemInstance problem, double time) {
			IMachine critMachine = getCriticalMachine(problem);
			IMachine bottleMachine = getBottleMachine(problem);

			double cwr = getCwr(machine, critMachine, bottleMachine, problem);

			double bestPriority = Double.NEGATIVE_INFINITY;
			IJob bestJob = null;

			DTDispatchingRule rule = getDispatchingRule(cwr);
			
			for (IJob job : machine.getWaitingJobs()) {
				JSSEvalData data = new JSSEvalData(problem, machine, job, time);
				
				double priority = rule.getNode().evaluate(data);
				if (priority > bestPriority) {
					bestPriority = priority;
					bestJob = job;
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

		private double getCwr(IMachine curMachine, IMachine critMachine, IMachine bottleMachine, IProblemInstance problem) {
			double totalWorkload = 0.0;
			double critWorkload = 0.0;

			for (IJob job : problem.getJobs()) {
				totalWorkload += job.getProcessingTime(bottleMachine);
				critWorkload += (job.isProcessable(critMachine) && curMachine.equals(critMachine)) ? job.getProcessingTime(bottleMachine) : 0.0;
			}

			return critWorkload / totalWorkload;
		}

		private DTDispatchingRule getDispatchingRule(double cwr) {
			if (cwr > cwrThreshold) {
				return rule1;
			} else {
				return rule2;
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

//	double earliestCompletionTime = Double.POSITIVE_INFINITY;
//	double earliestReadyTime = Double.POSITIVE_INFINITY;
//
//	for (IJob job : problem.getJobs()) {
//		if (!job.isProcessable(machine)) {
//			continue;
//		}
//
//		double readyTime;
//		if (job.getProcessingMachine() == null && machine.equals(job.getCurrentMachine())) {
//			readyTime = machine.getReadyTime();
//		} else if (job.getProcessingMachine() != null && machine.equals(job.getNextMachine())) {
//			readyTime = job.getProcessingMachine().getReadyTime();
//		} else {
//			continue;
//		}
//
//		double completionTime = readyTime + job.getProcessingTime(machine);
//
//		earliestReadyTime = Math.min(earliestReadyTime, readyTime);
//		earliestCompletionTime = Math.min(earliestCompletionTime, completionTime);
//	}
//
//	DTDispatchingRule rule = getDispatchingRule(cwr);
//
//	for (IJob job : problem.getJobs()) {
//		if (!job.isProcessable(machine)) {
//			continue;
//		}
//
//		double readyTime;
//		if (job.getProcessingMachine() == null && machine.equals(job.getCurrentMachine())) {
//			readyTime = machine.getReadyTime();
//		} else if (job.getProcessingMachine() != null && machine.equals(job.getNextMachine())) {
//			readyTime = job.getProcessingMachine().getReadyTime();
//		} else {
//			continue;
//		}
//
//		if (readyTime <= earliestReadyTime + rule.getAlpha() * (earliestCompletionTime - earliestReadyTime)) {
//			JSSEvalData data = new JSSEvalData(problem, machine, job, time);
//
//			double priority = rule.getNode().evaluate(data);
//			if (priority > bestPriority) {
//				bestPriority = priority;
//				bestJob = job;
//			}
//		}
//	}

}
