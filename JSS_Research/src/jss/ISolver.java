package jss;

import java.util.List;

/**
 * TODO I have no idea what I'm doing anymore.
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
