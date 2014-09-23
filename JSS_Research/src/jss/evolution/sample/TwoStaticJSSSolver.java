package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.problem.IJob;
import jss.problem.IMachine;
import jss.problem.IProblemInstance;
import jss.solver.EventCore;
import jss.solver.IAction;
import jss.solver.IRule;
import jss.solver.ISolver;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class TwoStaticJSSSolver implements ISolver {

	private IRule rule;

	/**
	 * Javadoc. I wonder if I even need this component here at all.
	 */
	public TwoStaticJSSSolver() {
	}

	public void setRule(IRule rule) {
		this.rule = rule;
	}

	@Override
	public List<IAction> getSolution(IProblemInstance problem) throws RuntimeException {
		EventCore core = new EventCore(problem, rule);
		List<IAction> solution = new ArrayList<IAction>();

		while (core.hasEvent()) {
			solution.add(core.triggerEvent());
		}

		return null;
	}
}
