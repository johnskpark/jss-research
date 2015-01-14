package jss.evolution.solver;

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
public class PriorityBasedDR extends JSSGPRule {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param inds
	 * @param threadnum
	 * @param data
	 */
	public PriorityBasedDR(EvolutionState state,
			GPIndividual[] inds,
			int threadnum,
			JSSGPData data) {
		super(state, inds, threadnum, data);
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
		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : machine.getWaitingJobs()) {
			getData().setProblem(problem);
			getData().setJob(job);
			getData().setMachine(machine);
			getData().setCurrentTime(time);

			getIndividuals()[0].trees[0].child.eval(getState(),
					getThreadnum(),
					getData(),
					null,
					getIndividuals()[0],
					null);

			if (getData().getPriority() > bestPriority) {
				bestPriority = getData().getPriority();
				bestJob = job;
			}
		}

		return bestJob;
	}

}
