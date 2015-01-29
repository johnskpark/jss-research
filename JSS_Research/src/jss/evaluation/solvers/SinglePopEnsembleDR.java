package jss.evaluation.solvers;

import java.util.List;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evaluation.JSSEvalData;
import jss.evaluation.node.INode;

public class SinglePopEnsembleDR implements IActionHandler {

	private List<INode> rules;
	private int ruleNum;

	public SinglePopEnsembleDR(List<INode> rules, int ruleNum) {
		this.rules = rules;
		this.ruleNum = ruleNum;
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
		List<IJob> processableJobs = machine.getWaitingJobs();

		double[][] priorities = new double[ruleNum][];
		double[] prioritySums = new double[processableJobs.size()];

		double bestPrioritySums = 0.0;
		int bestPrioritySumsIndex = -1;

		for (int i = 0; i < ruleNum; i++) {
			double[] normalisedPriorities = getNormalisedPriorities(rules.get(i),
					processableJobs,
					problem,
					machine,
					time);

			priorities[i] = normalisedPriorities;

			int bestIndex = getBestPriorityIndex(normalisedPriorities);

			prioritySums[bestIndex] += normalisedPriorities[bestIndex];

			if (prioritySums[bestIndex] > bestPrioritySums) {
				bestPrioritySums = prioritySums[bestIndex];
				bestPrioritySumsIndex = bestIndex;
			}
		}

		IJob mostVotedJob = processableJobs.get(bestPrioritySumsIndex);

		return mostVotedJob;
	}

	private int getBestPriorityIndex(double[] priorities) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		for (int i = 0; i < priorities.length; i++) {
			if (priorities[i] > bestPriority) {
				bestPriority = priorities[i];
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	private double[] getNormalisedPriorities(INode rule,
			List<IJob> processableJobs,
			IProblemInstance problem,
			IMachine machine,
			double time) {
		double[] normalisedPriorities = new double[processableJobs.size()];

		double[] priorities = new double[processableJobs.size()];
		double bestPriority = Double.NEGATIVE_INFINITY;
		double sum = 0.0;

		for (int i = 0; i < processableJobs.size(); i++) {
			IJob job = processableJobs.get(i);

			JSSEvalData data = new JSSEvalData(problem, machine, job, time);

			priorities[i] = rule.evaluate(data);
			if (priorities[i] > bestPriority) {
				bestPriority = priorities[i];
			}
		}

		for (int j = 0; j < processableJobs.size(); j++) {
			sum += Math.exp(priorities[j] - bestPriority);
		}

		for (int j = 0; j < processableJobs.size(); j++) {
			double normalisedPriority = Math.exp(priorities[j] - bestPriority - Math.log(sum));

			normalisedPriorities[j] = normalisedPriority;
		}

		return normalisedPriorities;
	}

}
