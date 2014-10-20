package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.Action;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
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
			JSSGPData data) {
		super(state, inds, threadnum, data);
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem) {
		List<IJob> processableJobs = new ArrayList<IJob>();

		for (IJob job : problem.getJobs()) {
			if (machine.equals(job.getNextMachine())) {
				processableJobs.add(job);
			}
		}

		if (processableJobs.isEmpty()) {
			return null;
		}

		int[] voteCounts = new int[processableJobs.size()];

		int mostVotes = 0;
		IJob mostVotedJob = null;

		for (int i = 0; i < getIndividuals().length; i++) {
			GPIndividual gpInd = getIndividuals()[i];

			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;

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

				if (getData().getPriority() > bestPriority) {
					bestPriority = getData().getPriority();
					bestIndex = j;
				}
			}

			if (bestIndex == -1) {
				return null;
			}

			voteCounts[bestIndex]++;
			if (voteCounts[bestIndex] > mostVotes) {
				mostVotes = voteCounts[bestIndex];
				mostVotedJob = processableJobs.get(bestIndex);
			}
		}

		// Simply process the job as early as possible.
		double time = Math.max(machine.getReadyTime(), mostVotedJob.getReadyTime(machine));
		return new Action(machine, mostVotedJob, time);
	}

}
