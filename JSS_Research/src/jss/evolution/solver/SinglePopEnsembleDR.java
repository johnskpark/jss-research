package jss.evolution.solver;

import java.util.List;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import jss.Action;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.ITracker;
import jss.evolution.JSSGPData;
import jss.evolution.JSSGPRule;
import jss.evolution.tracker.PriorityTracker;

public class SinglePopEnsembleDR extends JSSGPRule {

	private PriorityTracker tracker;

	public SinglePopEnsembleDR(EvolutionState state,
			GPIndividual[] inds,
			int threadnum,
			JSSGPData data,
			ITracker tracker) {
		super(state, inds, threadnum, data);

		this.tracker = (PriorityTracker) tracker;
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem, double time) {
		// TODO modify this to fit the implementation details that are in the paper. This includes the tracker and the weighted sum majority voting
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

		double[] normalisedPriorities = getNormalisedPriorities(getIndividuals()[index],
				processableJobs,
				problem,
				machine);

		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		for (int j = 0; j < normalisedPriorities.length; j++) {
			if (normalisedPriorities[j] > bestPriority) {
				bestPriority = normalisedPriorities[j];
				bestIndex = j;
			}
		}

		PriorityIndexPair pair = new PriorityIndexPair();
		pair.priority = bestPriority;
		pair.index = bestIndex;

		return pair;
	}

	private double[] getNormalisedPriorities(GPIndividual gpInd,
			List<IJob> processableJobs,
			IProblemInstance problem,
			IMachine machine) {
		double[] normalisedPriorities = new double[processableJobs.size()];
		double sumPriorities = 0.0;

		for (int j = 0; j < processableJobs.size(); j++) {
			getData().setProblem(problem);
			getData().setJob(processableJobs.get(j));
			getData().setMachine(machine);

			gpInd.trees[0].child.eval(getState(), getThreadnum(), getData(), null, gpInd, null);

			double sigmoidPriority = Math.exp(-getData().getPriority());

			normalisedPriorities[j] = sigmoidPriority;
			sumPriorities += sigmoidPriority;
		}

		for (int j = 0; j < normalisedPriorities.length; j++) {
			normalisedPriorities[j] /= sumPriorities;
		}

		return normalisedPriorities;
	}

	private class PriorityIndexPair {
		double priority;
		int index;
	}

}
