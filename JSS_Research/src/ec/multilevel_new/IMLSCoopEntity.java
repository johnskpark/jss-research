package ec.multilevel_new;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IMLSCoopEntity {

	/**
	 * Returns the fitness of the cooperative component.
	 */
	public Fitness getFitness();

	/**
	 * Returns the individuals which make up the cooperative component.
	 */
	public Individual[] getIndividuals();

	/**
	 * Combines the cooperative component with another to form a group.
	 */
	public IMLSCoopEntity combine(final EvolutionState state, final IMLSCoopEntity other);

}
