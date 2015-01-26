package jss.evolution.solver;

import java.util.List;

import jss.Action;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.ITracker;
import jss.evolution.JSSGPData;
import jss.evolution.JSSGPRule;
import jss.evolution.tracker.PriorityTracker;
import ec.EvolutionState;
import ec.gp.GPIndividual;

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
		List<IJob> processableJobs = machine.getWaitingJobs();

		double[][] priorities = new double[getIndividuals().length][];
		double[] prioritySums = new double[processableJobs.size()];

		double bestPrioritySums = 0.0;
		int bestPrioritySumsIndex = -1;

		getData().setProblem(problem);
		getData().setMachine(machine);
		getData().setCurrentTime(time);

		for (int i = 0; i < getIndividuals().length; i++) {
			double[] normalisedPriorities = getNormalisedPriorities(getIndividuals()[i],
					processableJobs,
					problem,
					machine,
					time);

			priorities[i] = normalisedPriorities;

			int bestIndex = getBestPriorityIndex(normalisedPriorities);

			prioritySums[bestIndex] += normalisedPriorities[bestIndex];

			if (prioritySums[bestIndex] > bestPrioritySums) {
				bestPrioritySums = prioritySums[bestIndex];
				bestPrioritySumsIndex = bestIndex;
			}
		}

		IJob mostVotedJob = processableJobs.get(bestPrioritySumsIndex);

		for (int i = 0; i < getIndividuals().length; i++) {
			tracker.addPriority(i, priorities[i][bestPrioritySumsIndex]);
		}

		return mostVotedJob;
	}

	private int getBestPriorityIndex(double[] priorities) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		for (int i = 0; i < priorities.length; i++) {
			if (priorities[i] > bestPriority) {
				bestPriority = priorities[i];
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	private double[] getNormalisedPriorities(GPIndividual gpInd,
			List<IJob> processableJobs,
			IProblemInstance problem,
			IMachine machine,
			double time) {
		double[] normalisedPriorities = new double[processableJobs.size()];

		double[] priorities = new double[processableJobs.size()];
		double bestPriority = Double.NEGATIVE_INFINITY;
		double sum = 0.0;

		for (int i = 0; i < processableJobs.size(); i++) {
			getData().setJob(processableJobs.get(i));

			gpInd.trees[0].child.eval(getState(), getThreadnum(), getData(), null, gpInd, null);

			priorities[i] = getData().getPriority();
			if (priorities[i] > bestPriority) {
				bestPriority = priorities[i];
			}
		}

		for (int j = 0; j < processableJobs.size(); j++) {
			sum += Math.exp(priorities[j] - bestPriority);
		}

		for (int j = 0; j < processableJobs.size(); j++) {
			double normalisedPriority = Math.exp(priorities[j] - bestPriority - Math.log(sum));

			normalisedPriorities[j] = normalisedPriority;
		}

		return normalisedPriorities;
	}

}
