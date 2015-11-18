package app.evolution;

import ec.EvolutionState;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface IJasimaFitness<T extends JasimaReproducible> {

	// FIXME Why do I even have this?
	public void setFitness(final EvolutionState state, final T individual);

	public void clear();

}
