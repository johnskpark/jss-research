package jss.evaluation.fitness;

import jss.IResult;
import jss.evaluation.IFitness;

/**
 * Fitness class, where the performance of the solution is measured by
 * the total weighted tardiness (TWT) value.
 * @author parkjohn
 *
 */
public class TWTFitness implements IFitness {

	@Override
	public double getFitness(IResult solution) {
		return solution.getMakespan();
	}

}
