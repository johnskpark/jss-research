package jss;

/**
 * Interface for the factories that generate problem instances.
 *
 * The problem instances are generated as if we are iterating over a series of problem.
 *
 * @author parkjohn
 *
 */
public interface ProblemInstanceFactory {

	/**
	 * Creates the next problem instance.
	 * @return The next problem instance, or null if no more problems can be generated.
	 */
	public ProblemInstance createNextProblemInstance();
}
