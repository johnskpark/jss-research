package jss.problem.static_problem;

import jss.ISubscriptionHandler;
import jss.problem.BaseMachine;

/**
 * A concrete representation of a machine in a static Job Shop Scheduling
 * problem instances.
 *
 * @see StaticInstance for definition of static Job Shop Scheduling problems.
 *
 * @author parkjohn
 *
 */
public class StaticMachine extends BaseMachine {

	/**
	 * TODO javadoc.
	 * @param id
	 * @param handler
	 */
	public StaticMachine(int id, ISubscriptionHandler handler) {
		super(id, handler);
	}

}
