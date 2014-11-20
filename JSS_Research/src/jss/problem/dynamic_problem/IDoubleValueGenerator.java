package jss.problem.dynamic_problem;

import jss.IJob;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface IDoubleValueGenerator {

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getDoubleValue(IJob job);

	/**
	 * TODO javadoc.
	 */
	public void reset();
}
