package jss.problem.dynamic_problem;

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
	 * @param problem
	 */
	public DynamicMachine(int id, DynamicInstance problem) {
		super(id, problem);
	}

}
