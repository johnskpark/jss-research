package jss.evaluation.fitness;

import jss.IResult;
import jss.evaluation.IFitness;

/**
 * Fitness class, where the performance of the solution is measured by
 * the makespan value.
 * @author parkjohn
 *
 */
public class MakespanFitness implements IFitness {

	@Override
	public double getFitness(IResult solution) {
		return solution.getTWT();
	}

}
