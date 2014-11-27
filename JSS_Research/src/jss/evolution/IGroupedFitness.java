package jss.evolution;

import jss.problem.Statistics;

/**
 * Represents the fitness of the individual in the GP population for grouped
 * problems.
 *
 * @author parkjohn
 *
 */
public interface IGroupedFitness {

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stats, int index);

}
