package jss.evolution.solvers;

import java.util.ArrayList;
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

	private PriorityTracker[] trackers;

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
			ITracker[] trackers) {
		super(state, inds, threadnum, data);

		this.trackers = new PriorityTracker[trackers.length];
		for (int i = 0; i < trackers.length; i++) {
			this.trackers[i] = (PriorityTracker)trackers[i];
		}
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem) {
		List<IJob> processableJobs = getProcessableJobs(machine, problem.getJobs());
		if (processableJobs.isEmpty()) {
			return null;
		}

		int[] voteCounts = new int[processableJobs.size()];
		double[] prioritySums = new double[processableJobs.size()];

		int mostVotes = 0;
		int mostVotedIndex = -1;

		for (int i = 0; i < getIndividuals().length; i++) {
			GPIndividual gpInd = getIndividuals()[i];

			PriorityIndexPair bestPair = getBestIndex(gpInd, processableJobs, machine, problem, trackers[i]);
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
		double time = Math.max(machine.getReadyTime(), mostVotedJob.getReadyTime(machine));
		return new Action(machine, mostVotedJob, time);
	}

	// Get the list of jobs that are waiting to be processed on the input machine.
	private List<IJob> getProcessableJobs(IMachine machine, List<IJob> jobs) {
		List<IJob> processableJobs = new ArrayList<IJob>();
		for (IJob job : jobs) {
			if (machine.equals(job.getNextMachine())) {
				processableJobs.add(job);
			}
		}
		return processableJobs;
	}

	// Get the index of the job with the highest priority.
	private PriorityIndexPair getBestIndex(GPIndividual gpInd,
			List<IJob> processableJobs,
			IMachine machine,
			IProblemInstance problem,
			PriorityTracker tracker) {
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
			tracker.getPriorities().add(getData().getPriority());

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
