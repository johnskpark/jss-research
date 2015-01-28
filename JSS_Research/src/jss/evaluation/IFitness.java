package jss.evaluation;

import jss.IResult;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface IFitness {

	/**
	 * TODO javadoc.
	 * @param solution
	 * @return
	 */
	public double getFitness(IResult solution);

}
