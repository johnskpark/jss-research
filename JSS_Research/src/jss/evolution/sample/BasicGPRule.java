package jss.evolution.sample;

import java.util.List;

import jss.Action;
import jss.ActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public class BasicGPRule implements ActionHandler {

	private EvolutionState state;
	private GPIndividual ind;
	private int subpopulation;
	private int threadnum;

	private BasicData data;

	public BasicGPRule(EvolutionState state,
			GPIndividual ind,
			int subpopulation,
			int threadnum,
			BasicData data) {
		this.state = state;
		this.ind = ind;
		this.subpopulation = subpopulation;
		this.threadnum = threadnum;

		this.data = data;
	}

	@Override
	public Action getAction(IMachine machine, IProblemInstance problem) {
		List<IJob> jobs = problem.getJobs();

		double bestPriority = Double.NEGATIVE_INFINITY;
		IJob bestJob = null;

		for (IJob job : jobs) {
			if (!job.isProcessable(machine)) {
				continue;
			}

			data.setJob(job);
			data.setMachine(machine);

			ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

			if (data.getPriority() > bestPriority) {
				bestPriority = data.getPriority();
				bestJob = job;
			}
		}

		if (bestJob != null) {
			// Simply process the job as early as possible.
			double time = Math.max(machine.getTimeAvailable(), bestJob.getReleaseTime());
			return new Action(machine, bestJob, time);
		} else {
			return null;
		}
	}

}
