package ec.multilevel;

import app.evolution.JasimaReproducible;
import ec.EvolutionState;
import ec.Individual;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IMLSCoopEntity extends JasimaReproducible {

	/**
	 * Returns the individuals which make up the cooperative component.
	 */
	public Individual[] getIndividuals();

	/**
	 * Combines the cooperative component with another to form a group.
	 */
	public IMLSCoopEntity combine(final EvolutionState state, final IMLSCoopEntity other);

}
