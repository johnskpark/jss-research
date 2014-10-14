package jss;

/**
 * Interface for the solvers that generate solutions for Job Shop Scheduling
 * problem instances.
 *
 * A solution in this case is defined as a list of @see Action that can be
 * applied sequentially to the problem instance.
 *
 * ISolver should be used in conjunction with @see ISubscriber to solve the
 * job shop scheduling problem instances.
 *
 * @author parkjohn
 *
 */
public interface ISolver {

	/**
	 * Use the solver to generate a solution for the particular job shop
	 * scheduling problem instance.
	 * @param problem The job shop scheduling problem instance to solve.
	 * @return The solution for the problem instance as an instance of IResult.
	 * @throws RuntimeException TODO this will probably be modified later down the line.
	 */
	public IResult getSolution(IProblemInstance problem) throws RuntimeException;
}
