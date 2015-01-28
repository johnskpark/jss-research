package jss.evaluation.fitness;

import jss.IResult;
import jss.evaluation.IFitness;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MakespanFitness implements IFitness {

	@Override
	public double getFitness(IResult solution) {
		return solution.getTWT();
	}

}
