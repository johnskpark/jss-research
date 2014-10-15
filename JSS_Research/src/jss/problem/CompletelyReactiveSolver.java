package jss.problem;

import jss.Action;
import jss.IActionHandler;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;
import jss.ISubscriber;
import jss.ISubscriptionHandler;
import jss.Simulator;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CompletelyReactiveSolver implements ISolver, ISubscriber {

	private IActionHandler rule;

	private IProblemInstance problem;
	private Result solution;

	/**
	 * TODO Javadoc. I wonder if I even need this component here at all.
	 */
	public CompletelyReactiveSolver() {
	}

	/**
	 * TODO javadoc.
	 * @param rule
	 */
	public void setRule(IActionHandler rule) {
		this.rule = rule;
	}

	@Override
	public IResult getSolution(IProblemInstance problem) throws RuntimeException {
		Simulator core = new Simulator(problem);
		this.problem = problem;
		this.solution = new Result(problem);

		// TODO fix this up sometime later.
		((ISubscriptionHandler)problem).onSubscriptionRequest(this);;

		// Run the simulator.
		while (core.hasEvent()) {
			core.triggerEvent();
		}

		problem.reset();

		return solution;
	}

	@Override
	public void onMachineFeed(IMachine m, double time) {
		// Cycle through the machines and process jobs on the available machines
		for (IMachine machine : problem.getMachines()) {
			if (!machine.isAvailable()) {
				continue;
			}

			IJob lastJob;
			if ((lastJob = machine.getLastProcessedJob()) != null) {
				double completionTime = machine.getTimeAvailable();
				double penalty = lastJob.getPenalty(machine);
				double tardiness = Math.max(completionTime -
						lastJob.getDueDate(machine), 0);

				solution.setMakespan(completionTime);
				solution.setTWT(solution.getTWT() + penalty * tardiness);
			}

			Action action = rule.getAction(machine, problem);

			if (action != null) {
				solution.addAction(action);
				machine.processJob(action.getJob(), time);
			}
		}
	}

	@Override
	public void onJobFeed(IJob job, double time) {
		// Do nothing. TODO make it do something IF the job is being
		// released.
	}

}
