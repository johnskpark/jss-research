package jss;

/**
 * Interface for the factories that generate problem instances.
 *
 * @author parkjohn
 *
 */
public interface IProblemFactory extends Iterable<IProblemInstance> {

	/**
	 * Create the next problem instance.
	 *
	 * TODO I'm wondering if I should even keep this.
	 * @return
	 */
	public IProblemInstance createNextProblem();


}
