package jss.problem.dynamic_problem;

import java.util.List;
import java.util.Set;

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
	public List<IMachine> getProcessingOrder(Set<? extends IMachine> machine);

	/**
	 * TODO javadoc.
	 */
	public void reset();

}
