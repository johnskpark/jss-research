package jss.evolution.sample;

import jss.Action;
import jss.ActionHandler;
import jss.EventCore;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.IResult;
import jss.ISolver;
import jss.Subscriber;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class TwoStaticJSSSolver implements ISolver, Subscriber {

	private ActionHandler rule;

	private IProblemInstance problem;
	private BasicResult solution;

	/**
	 * TODO Javadoc. I wonder if I even need this component here at all.
	 */
	public TwoStaticJSSSolver() {
	}

	public void setRule(ActionHandler rule) {
		this.rule = rule;
	}

	@Override
	public IResult getSolution(IProblemInstance problem) throws RuntimeException {
		EventCore core = new EventCore(problem);
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
			 // TODO need to set the makespan and total weighted tardiness
			solution.setMakespan(machine.getTimeAvailable());
			solution.setTWT(0); // TODO

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
