package jss;

/**
 * Interface for the factories that generate problem instances.
 * TODO currently not used anywhere at the moment.
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
