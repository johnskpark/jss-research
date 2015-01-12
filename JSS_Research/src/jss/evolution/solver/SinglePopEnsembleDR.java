package jss.evolution.solver;

import java.util.ArrayList;
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
		if (machine.getWaitingJobs().isEmpty()) {
			return null;
		}

		List<IJob> processableJobs = machine.getWaitingJobs();
		List<List<Double>> priorities = new ArrayList<List<Double>>();

		double[] prioritySums = new double[processableJobs.size()];

		double bestPrioritySums = 0.0;
		int bestPrioritySumsIndex = -1;

		for (int i = 0; i < getIndividuals().length; i++) {
			List<Double> normalisedPriorities = getNormalisedPriorities(getIndividuals()[i],
					processableJobs,
					problem,
					machine,
					time);

			priorities.add(normalisedPriorities);

			int bestIndex = getBestPriorityIndex(normalisedPriorities);

			prioritySums[bestIndex] += normalisedPriorities.get(bestIndex);

			if (prioritySums[bestIndex] > bestPrioritySums) {
				bestPrioritySums = prioritySums[bestIndex];
				bestPrioritySumsIndex = bestIndex;
			}
		}

		IJob mostVotedJob = processableJobs.get(bestPrioritySumsIndex);

		for (int i = 0; i < getIndividuals().length; i++) {
			tracker.addPriority(i, priorities.get(i).get(bestPrioritySumsIndex));
		}

		// Simply process the job as early as possible.
		double t = Math.max(machine.getReadyTime(), mostVotedJob.getReadyTime());
		return new Action(machine, mostVotedJob, t);
	}

	private int getBestPriorityIndex(List<Double> priorities) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		int bestIndex = -1;

		for (int i = 0; i < priorities.size(); i++) {
			if (priorities.get(i) > bestPriority) {
				bestPriority = priorities.get(i);
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	private List<Double> getNormalisedPriorities(GPIndividual gpInd,
			List<IJob> processableJobs,
			IProblemInstance problem,
			IMachine machine,
			double time) {
		List<Double> normalisedPriorities = new ArrayList<Double>(processableJobs.size());

		double[] priorities = new double[processableJobs.size()];
		double bestPriority = Double.NEGATIVE_INFINITY;
		double sum = 0.0;

		for (int i = 0; i < processableJobs.size(); i++) {
			getData().setProblem(problem);
			getData().setJob(processableJobs.get(i));
			getData().setMachine(machine);
			getData().setCurrentTime(time);

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

			normalisedPriorities.add(j, normalisedPriority);
		}

		return normalisedPriorities;
	}

}
