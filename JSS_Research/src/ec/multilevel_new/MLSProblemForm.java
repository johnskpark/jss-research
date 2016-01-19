package ec.multilevel_new;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public interface MLSProblemForm {

	/**
	 * TODO javadoc.
	 * @param state
	 * @param pop
	 */
	public void beforeEvaluation(final EvolutionState state, Population pop);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param pop
	 */
	public void afterEvaluation(final EvolutionState state, Population pop);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param updateFitness
	 * @param countVictoriesOnly
	 * @param subpops
	 * @param threadnum
	 */
	public void evaluateGroup(final EvolutionState state,
			MLSSubpopulation subpop,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param subpopulation
	 * @param threadnum
	 */
	public void evaluateInd(final EvolutionState state,
			Individual ind,
			final int subpopulation,
			final int threadnum);
}
