package jss.evolution.sample;

import jss.Action;
import jss.ActionHandler;
import jss.Simulator;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;
import jss.ISubscriber;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class BasicSolver implements ISolver, ISubscriber {

	private ActionHandler rule;

	private IProblemInstance problem;
	private BasicResult solution;

	/**
	 * TODO Javadoc. I wonder if I even need this component here at all.
	 */
	public BasicSolver() {
	}

	public void setRule(ActionHandler rule) {
		this.rule = rule;
	}

	@Override
	public IResult getSolution(IProblemInstance problem) throws RuntimeException {
		Simulator core = new Simulator(problem);
		this.problem = problem;
		this.solution = new BasicResult(problem);

		while (core.hasEvent()) {
			core.triggerEvent();
		}

		problem.reset();

		return solution;
	}

	@Override
	public void onMachineFeed(IMachine machine) {
		if (machine.isAvailable()) {
			IJob lastJob = machine.getLastProcessedJob();

			double penalty = lastJob.getPenalty(machine);
			double tardiness = Math.max(machine.getTimeAvailable() -
					lastJob.getDueDate(machine), 0);

			solution.setMakespan(machine.getTimeAvailable());
			solution.setTWT(solution.getTWT() + penalty * tardiness);

			Action action = rule.getAction(machine, problem);
			solution.addAction(action);
			machine.processJob(action.getJob());
		}
	}

	@Override
	public void onJobFeed(IJob job) {
		// Do nothing.
	}

}
