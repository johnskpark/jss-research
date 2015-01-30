package jss.evolution;

import java.util.List;

import jss.IProblemInstance;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

/**
 * Represents the fitness of the individual in the GP population for simple
 * problems.
 *
 * @author parkjohn
 *
 */
public interface ISimpleFitness extends Setup {

	/**
	 * Load in the training set if the fitness measure requires it for certain
	 * calculations (e.g. calculating upper bound).
	 * @param problems
	 */
	public void loadDataset(List<IProblemInstance> problems);

	/**
	 * Get the fitness from the solution statistics obtained from a training run.
	 */
	public double getFitness(Statistics stats);

	/**
	 * Set the fitness of the individual from the statistic obtained from
	 * the training run.
	 */
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats);
}
