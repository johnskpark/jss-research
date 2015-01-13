package jss.problem.dynamic_problem;

import jss.ISubscriptionHandler;
import jss.problem.BaseMachine;

/**
 * A concrete representation of a machine in a dynamic Job Shop Scheduling
 * problem instances.
 *
 * @see DynamicInstance for definition of dynamic Job Shop Scheduling problems.
 *
 * @author parkjohn
 *
 */
public class DynamicMachine extends BaseMachine {

	/**
	 * TODO javadoc.
	 * @param id
	 * @param handler
	 */
	public DynamicMachine(int id, ISubscriptionHandler handler) {
		super(id, handler);
	}

}
