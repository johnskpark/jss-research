package jss.problem.breakdown_problem;

import jss.ISubscriptionHandler;
import jss.problem.BaseMachine;

/**
 * TODO currently just a copy of the static machine. If we don't require
 * the separation, merge the two into one class.
 *
 * @author parkjohn
 *
 */
public class BreakdownMachine extends BaseMachine {

	/**
	 * TODO javadoc.
	 * @param id
	 * @param problem
	 */
	public BreakdownMachine(int id, BreakdownInstance problem) {
		super(id, problem);
	}

}
