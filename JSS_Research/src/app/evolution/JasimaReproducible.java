package app.evolution;

import ec.Fitness;

/**
 * TODO I really need to come up with a better name for this later down the line.
 *
 * @author parkjohn
 *
 */
public interface JasimaReproducible {

	/**
	 * Returns the fitness of the reproducible component.
	 */
	public Fitness getFitness();

	/**
	 * Returns whether the reproducible component has been evaluated.
	 */
	public boolean isEvaluated();

	/**
	 * Set whether the reproducible component has been evaluated.
	 */
	public void setEvaluated(boolean evaluated);

}
