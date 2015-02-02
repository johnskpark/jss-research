package jss.evolution;

import jss.problem.Statistics;

/**
 * Represents the fitness of the individual in the GP population for simple
 * problems.
 *
 * @author parkjohn
 *
 */
public interface ISimpleFitness extends IFitness {

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stats);

}
