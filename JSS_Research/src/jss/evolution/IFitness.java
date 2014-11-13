package jss.evolution;

import jss.problem.Statistics;
import ec.Individual;

/**
 * Represents the fitness of the individual in the GP population.
 *
 * @author parkjohn
 *
 */
public interface IFitness {

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stats, Individual ind);

}
