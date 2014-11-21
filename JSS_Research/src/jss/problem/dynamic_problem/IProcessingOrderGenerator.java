package jss.problem.dynamic_problem;

import java.util.List;

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
	public List<DynamicMachine> getProcessingOrder(List<DynamicMachine> machine);

	/**
	 * TODO javadoc.
	 */
	public void reset();

}
