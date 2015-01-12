package jss.evolution;

import java.util.List;

import jss.IProblemInstance;
import jss.problem.Statistics;

/**
 * Represents the fitness of the individual in the GP population for simple
 * problems.
 *
 * @author parkjohn
 *
 */
public interface ISimpleFitness {

	/**
	 * TODO javadoc.
	 * @param problems
	 */
	public void loadDataset(List<IProblemInstance> problems);

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stat);
}
