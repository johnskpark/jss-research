package jss.problem.static_problem;

import java.util.List;

import jss.IDataset;

/**
 * TODO need a way to automatically generate the data, and then write it down
 * to a file (and be able to read it again).
 *
 * @author parkjohn
 *
 */
public abstract class StaticDataset implements IDataset {

	/**
	 * TODO javadoc. Also, change this so that the static instance has the upper bound instead of the dataset.
	 * @return
	 */
	public abstract List<Double> getUpperBounds();

	/**
	 * TODO javadoc.Also, change this so that the static instance has the lower bound instead of the dataset.
	 * @return
	 */
	public abstract List<Double> getLowerBounds();

}
