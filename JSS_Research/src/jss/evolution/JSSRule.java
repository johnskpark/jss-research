package jss.evolution;

import java.util.List;

import jss.Action;
import jss.ActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import ec.EvolutionState;
import ec.gp.GPIndividual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JSSRule implements ActionHandler {

	private EvolutionState state;
	private GPIndividual ind;
	private int threadnum;

	private JSSData data;

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param threadnum
	 * @param data
	 */
	public JSSRule(EvolutionState state,
			GPIndividual ind,
			int threadnum,
			JSSData data) {
		this.state = state;
		this.ind = ind;
		this.threadnum = threadnum;

		this.data = data;
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

			data.setProblem(problem);
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
