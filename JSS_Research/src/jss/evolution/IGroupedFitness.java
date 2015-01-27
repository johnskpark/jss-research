package jss.evolution;

import jss.problem.Statistics;
import ec.Setup;

/**
 * Represents the fitness of the individual in the GP population for grouped
 * problems.
 *
 * @author parkjohn
 *
 */
public interface IGroupedFitness extends Setup {

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stats, int index);

}
