package jss.evolution.fitness;

import java.util.List;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;
import jss.evolution.ISimpleFitness;
import jss.problem.CompletelyReactiveSolver;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class HildebrandtFitness implements ISimpleFitness {

	private static final long serialVersionUID = -4145970305812990734L;

	private ISolver benchmarkSolver;
	private Statistics benchmarkStats;
	private boolean benchmarkLoaded;

	private List<IProblemInstance> problems;

	/**
	 * TODO javadoc.
	 */
	public HildebrandtFitness() {
		CompletelyReactiveSolver solver = new CompletelyReactiveSolver();
		solver.setRule(new RajendranRule());

		benchmarkSolver = solver;
		benchmarkStats = new Statistics();
		benchmarkLoaded = false;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
		this.problems = problems;

		for (IProblemInstance problem : problems) {
			IResult solution = benchmarkSolver.getSolution(problem);

			benchmarkStats.addSolution(problem, solution);
		}

		benchmarkLoaded = true;
	}

	@Override
	public double getFitness(Statistics stat) {
		if (!benchmarkLoaded) {
			loadDataset(stat.getProblems());
		}

		double performanceIndex = 0;
		double fullSystemPenalty = 0;
		int benchmarkNumJobs = 0, numJobs = 0;

		for (IProblemInstance problem : stat.getProblems()) {
			IResult benchmarkResults = benchmarkStats.getSolution(problem);
			IResult results = stat.getSolution(problem);

			performanceIndex += benchmarkResults.getTWT() / results.getTWT();

			// TODO fix these up later, since they won't work for breakdown scenarios.
			benchmarkNumJobs += benchmarkResults.getActions().size();
			numJobs = results.getActions().size();
		}

		performanceIndex /= stat.getProblems().size();
		fullSystemPenalty = 1.0 / (Math.min(0.9, 1.0 * numJobs / benchmarkNumJobs));

		return performanceIndex * fullSystemPenalty;
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats) {
		double fitness = getFitness(stats);

		((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<IProblemInstance> getProblems() {
		return problems;
	}

	// TODO documentation.
	private class RajendranRule implements IActionHandler {

		@Override
		public Action getAction(IMachine machine, IProblemInstance problem, double time) {
			double bestPriority = Double.NEGATIVE_INFINITY;
			IJob bestJob = null;

			for (IJob job : machine.getWaitingJobs()) {
				if (!machine.equals(job.getCurrentMachine())) {
					continue;
				}

				double priority = calculatePriority(machine, job);

				if (priority > bestPriority) {
					bestPriority = priority;
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

		private double calculatePriority(IMachine machine, IJob job) {
			double pt = job.getProcessingTime(machine);
			double winq = 0;
			double npt = 0;

			IMachine nextMachine = job.getNextMachine();
			if (nextMachine != null) {
				for (IJob waitingJob : nextMachine.getWaitingJobs()) {
					winq += waitingJob.getProcessingTime(nextMachine);
				}

				IJob currentJob;
				if ((currentJob = nextMachine.getCurrentJob()) != null) {
					winq += currentJob.getProcessingTime(nextMachine);
				}

				npt = job.getProcessingTime(nextMachine);
			}

			return -(2 * pt + winq + npt);
		}

	}

}
