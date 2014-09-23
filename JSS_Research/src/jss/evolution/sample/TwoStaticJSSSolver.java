package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.Action;
import jss.ActionHandler;
import jss.EventCore;
import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
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
	private List<Action> solution;

	/**
	 * Javadoc. I wonder if I even need this component here at all.
	 */
	public TwoStaticJSSSolver() {
	}

	public void setRule(ActionHandler rule) {
		this.rule = rule;
	}

	@Override
	public List<Action> getSolution(IProblemInstance problem) throws RuntimeException {
		EventCore core = new EventCore(problem);
		this.problem = problem;
		this.solution = new ArrayList<Action>();

		while (core.hasEvent()) {
			core.triggerEvent();
		}

		return solution;
	}

	@Override
	public void onMachineFeed(IMachine machine) {
		if (machine.isAvailable()) {
			solution.add(rule.getAction(machine, problem));
		}
	}

	@Override
	public void onJobFeed(IJob job) {
		// Do nothing.
	}
}
