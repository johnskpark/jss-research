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
public class CoopEnsembleDR implements IActionHandler {

	private List<INode> rules;
	private int ruleNum;

	/**
	 * TODO javadoc.
	 * @param rules
	 * @param ruleNum
	 */
	public CoopEnsembleDR(List<INode> rules, int ruleNum) {
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

		int[] voteCounts = new int[processableJobs.size()];
		double[] prioritySums = new double[processableJobs.size()];

		int mostVotes = 0;
		int mostVotedIndex = -1;

		for (int i = 0; i < ruleNum; i++) {
			INode rule = rules.get(i);

			PriorityIndexPair bestPair = getBestIndex(rule, processableJobs, machine, problem);
			if (bestPair.index == -1) {
				return null;
			}

			voteCounts[bestPair.index]++;
			prioritySums[bestPair.index] += bestPair.priority;

			if (voteCounts[bestPair.index] > mostVotes || (voteCounts[bestPair.index] == mostVotes ||
					prioritySums[bestPair.index] > prioritySums[mostVotedIndex])) {
				mostVotes = voteCounts[bestPair.index];
				mostVotedIndex = bestPair.index;
			}
		}

		IJob mostVotedJob = processableJobs.get(mostVotedIndex);
		return mostVotedJob;
	}

	// Get the index of the job with the highest priority.
	private PriorityIndexPair getBestIndex(INode rule,
			List<IJob> processableJobs,
			IMachine machine,
			IProblemInstance problem) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		double[] normalisedPriorities = new double[processableJobs.size()];

		for (int j = 0; j < processableJobs.size(); j++) {
			IJob job = processableJobs.get(j);

			JSSEvalData data = new JSSEvalData(problem, machine, job);
			double priority = rule.evaluate(data);

			// Normalise the priority between interval [0,1]
			normalisedPriorities[j] = 1.0 / (1.0 + Math.exp(-priority));

			// Update the best priority.
			if (priority > bestPriority) {
				bestPriority = priority;
				bestIndex = j;
			}
		}

		double sumNormalisedPriorities = 0.0;
		for (int j = 0; j < processableJobs.size(); j++) {
			sumNormalisedPriorities += normalisedPriorities[j];
		}

		PriorityIndexPair pair = new PriorityIndexPair();
		pair.priority = normalisedPriorities[bestIndex] / sumNormalisedPriorities;
		pair.index = bestIndex;
		return pair;
	}

	private class PriorityIndexPair {
		double priority;
		int index;
	}

}
