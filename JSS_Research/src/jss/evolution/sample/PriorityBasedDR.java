package jss.evolution.sample;

import java.util.List;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import jss.Action;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.JSSGPData;
import jss.evolution.JSSGPRule;

public class PriorityBasedDR extends JSSGPRule {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param threadnum
	 * @param data
	 */
	public PriorityBasedDR(EvolutionState state,
			GPIndividual ind,
			int threadnum,
			JSSGPData data) {
		super(state, ind, threadnum, data);
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem) {
		List<IJob> jobs = problem.getJobs();

		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : jobs) {
			if (!machine.equals(job.getNextMachine())) {
				continue;
			}

			getData().setProblem(problem);
			getData().setJob(job);
			getData().setMachine(machine);

			getIndividual().trees[0].child.eval(getState(),
					getThreadnum(),
					getData(),
					null,
					getIndividual(),
					null);

			if (getData().getPriority() > bestPriority) {
				bestPriority = getData().getPriority();
				bestJob = job;
			}
		}

		if (bestJob != null) {
			// Simply process the job as early as possible.
			double time = Math.max(machine.getTimeAvailable(), bestJob.getReadyTime(machine));
			return new Action(machine, bestJob, time);
		} else {
			return null;
		}
	}

}
