package jss.problem.dynamic_problem;

import java.util.List;

import jss.IMachine;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface IProcessingOrderGenerator {

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<IMachine> getProcessingOrder();

	/**
	 * TODO javadoc.
	 */
	public void reset();

}
