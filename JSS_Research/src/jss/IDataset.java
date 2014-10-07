package jss;

import java.util.List;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface IDataset {

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<IProblemInstance> getProblems();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<IProblemInstance> getTraining();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<IProblemInstance> getTest();
}
