package jss.solver;

import jss.problem.IMachine;
import jss.problem.IProblemInstance;

/**
 * Generates an action from the input. TODO elaborate.
 *
 * @author parkjohn
 *
 */
public interface IRule {

	/**
	 * Get the action for the particular machine, given a problem instance.
	 * @return
	 */
	public IAction getAction(IMachine machine, IProblemInstance problem);

}
