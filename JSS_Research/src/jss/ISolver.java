package jss;

/**
 * Interface for the solvers that generate solutions for Job Shop Scheduling
 * problem instances.
 *
 * A solution in this case is defined as a list of @see Action that can be
 * applied sequentially to the problem instance.
 *
 * TODO more documentation.
 *
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
	public IResult getSolution(IProblemInstance problem) throws RuntimeException;
}
