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
		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : machine.getWaitingJobs()) {
			if (!machine.equals(job.getNextMachine())) {
				continue;
			}

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

		if (bestJob != null) {
			// Simply process the job as early as possible.
			double t = Math.max(machine.getReadyTime(), bestJob.getReadyTime());
			return new Action(machine, bestJob, t);
		} else {
			return null;
		}
	}

}
