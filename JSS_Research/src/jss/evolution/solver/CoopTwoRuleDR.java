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
public class CoopTwoRuleDR extends JSSGPRule {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param inds
	 * @param threadnum
	 * @param data
	 */
	public CoopTwoRuleDR(EvolutionState state,
			GPIndividual[] inds,
			int threadnum,
			JSSGPData data) {
		super(state, inds, threadnum, data);
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem, double time) {
		if (selectFirstRule(machine, problem, time)) {
			return getAction(getIndividuals()[0], machine, problem);
		} else {
			return getAction(getIndividuals()[1], machine, problem);
		}
	}

	// Determine whether to use the first rule out of the two rules.
	private boolean selectFirstRule(IMachine machine, IProblemInstance problem, double time) {
		return time == 0;
	}

	// Get the action using the specified rule
	private Action getAction(GPIndividual gpInd, IMachine machine, IProblemInstance problem) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : machine.getWaitingJobs()) {
			if (!machine.equals(job.getNextMachine())) {
				continue;
			}

			getData().setProblem(problem);
			getData().setJob(job);
			getData().setMachine(machine);

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
			double time = Math.max(machine.getReadyTime(), bestJob.getReadyTime());
			return new Action(machine, bestJob, time);
		} else {
			return null;
		}
	}

}
