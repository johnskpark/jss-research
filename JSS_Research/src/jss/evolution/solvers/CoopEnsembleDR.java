package jss.evolution.solvers;

import java.util.List;

import jss.Action;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.ITracker;
import jss.evolution.JSSGPData;
import jss.evolution.JSSGPRule;
import ec.EvolutionState;
import ec.gp.GPIndividual;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleDR extends JSSGPRule {

	private PriorityTracker tracker;

	/**
	 * TODO javadoc.
	 * @param state
	 * @param inds
	 * @param threadnum
	 * @param data
	 */
	public CoopEnsembleDR(EvolutionState state,
			GPIndividual[] inds,
			int threadnum,
			JSSGPData data,
			ITracker tracker) {
		super(state, inds, threadnum, data);

		this.tracker = (PriorityTracker) tracker;
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem, double time) {
		if (machine.getWaitingJobs().isEmpty()) {
			return null;
		}

		List<IJob> processableJobs = machine.getWaitingJobs();

		int[] voteCounts = new int[processableJobs.size()];
		double[] prioritySums = new double[processableJobs.size()];

		int mostVotes = 0;
		int mostVotedIndex = -1;

		for (int i = 0; i < getIndividuals().length; i++) {
			PriorityIndexPair bestPair = getBestIndex(i, processableJobs, machine, problem, tracker);
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

		// Simply process the job as early as possible.
		double t = Math.max(machine.getReadyTime(), mostVotedJob.getReadyTime());
		return new Action(machine, mostVotedJob, t);
	}

	// Get the index of the job with the highest priority.
	private PriorityIndexPair getBestIndex(int index,
			List<IJob> processableJobs,
			IMachine machine,
			IProblemInstance problem,
			PriorityTracker tracker) {
		GPIndividual gpInd = getIndividuals()[index];

		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		double[] normalisedPriorities = new double[processableJobs.size()];

		for (int j = 0; j < processableJobs.size(); j++) {
			IJob job = processableJobs.get(j);

			getData().setProblem(problem);
			getData().setJob(job);
			getData().setMachine(machine);

			gpInd.trees[0].child.eval(getState(),
					getThreadnum(),
					getData(),
					null,
					gpInd,
					null);

			// Normalise the priority between interval [0,1]
			normalisedPriorities[j] = 1.0 / (1.0 + Math.exp(-getData().getPriority()));

			// Add the priority to the trackers
			tracker.addPriority(index, getData().getPriority());

			// Update the best priority.
			if (getData().getPriority() > bestPriority) {
				bestPriority = getData().getPriority();
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
