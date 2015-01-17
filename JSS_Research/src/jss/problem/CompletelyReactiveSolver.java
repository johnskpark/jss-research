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
 * Represents a completely reactive solver that generates solutions for Job
 * Shop Scheduling problem instances.
 *
 * A completely reactive solver is a solver that assigns an @see Action for a
 * particular machine as soon as the machine finishes processing a job.
 * Because of this, dispatching rules are considered as being completely
 * reactive solver. This differs from predictive reactive solvers, where the
 * schedule predefined before the simulation starts, and modified as
 * stochastic processes occur during the simulation (e.g. machines breaking
 * down).
 *
 * Completely reactive solvers are considered as being able to handle
 * dynamic/stochastic Job Shop Scheduling problems better due to its
 * on-the-spot decision making procedure. However, it is also far more myopic
 * than predictive reactive solvers.
 *
 * @author parkjohn
 *
 */
public class CompletelyReactiveSolver implements ISolver, ISubscriber {

	private IActionHandler rule;

	private IProblemInstance problem;
	private Result solution;

	/**
	 * Generate a new instance of a completely reactive solver.
	 */
	public CompletelyReactiveSolver() {
	}

	/**
	 * Set the rule that will be used for the 'on-the-spot' decision making.
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
		this.solution.setWarmUp(problem.getWarmUp());

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
		processOperation(m);

		// Cycle through the machines and process jobs on the available machines.
		for (IMachine machine : problem.getAvailableMachines()) {
			assignAction(machine, time);
		}
	}

	private void processOperation(IMachine machine) {
		IJob lastJob = machine.getLastProcessedJob();

		if ((lastJob = machine.getLastProcessedJob()) != null) {
			double completionTime = machine.getReadyTime();

			solution.setMakespan(completionTime);

			if (lastJob.isCompleted()) {
				double penalty = lastJob.getPenalty();
				double tardiness = Math.max(completionTime - lastJob.getDueDate(), 0);

				solution.addTWT(penalty * tardiness);
				solution.increment();
			}
		}
	}

	@Override
	public void onJobFeed(IJob job, double time) {
		// Find the machine that the job was released for.
		IMachine machine = job.getCurrentMachine();

		// Process the job if the machine is available.
		if (machine.isAvailable()) {
			assignAction(machine, time);
		}

	}

	private void assignAction(IMachine machine, double time) {
		Action action = rule.getAction(machine, problem, time);

		if (action != null) {
			solution.addAction(action);
			machine.processJob(action.getJob(), time);
		}
	}

}
