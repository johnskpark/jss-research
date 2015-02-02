package jss.evolution;

import java.util.List;

import jss.IProblemInstance;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Setup;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public interface IFitness extends Setup {

	/**
	 * Load in the training set if the fitness measure requires it for certain
	 * calculations (e.g. calculating upper bound).
	 * @param problems
	 */
	public void loadDataset(List<IProblemInstance> problems);

	/**
	 * Set the fitness of the individual from the statistic obtained from
	 * the training run.
	 */
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats);
}
