package jss.solver;

import java.util.List;

import jss.problem.IProblemInstance;

/**
 * TODO I have no idea what I'm doing anymore.
 * @author parkjohn
 *
 */
public interface ISolver {

	/**
	 * TODO javadoc.
	 * @param problem
	 * @return
	 * @throws RuntimeException
	 */
	public List<IAction> getSolution(IProblemInstance problem) throws RuntimeException;
}
