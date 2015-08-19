package app.evolution.multilevel;

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
	 * @param prepareForFitnessAssessment
	 * @param countVictoriesOnly
	 */
	public void preprocessPopulation(final EvolutionState state, Population pop, final boolean[] prepareForFitnessAssessment, final boolean countVictoriesOnly);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param pop
	 * @param assessFitness
	 * @param countVictoriesOnly
	 */
	public void postprocessPopulation(final EvolutionState state, Population pop, final boolean[] assessFitness, final boolean countVictoriesOnly);

	/**
	 * TODO javadoc.
	 * @param state
	 * @param ind
	 * @param updateFitness
	 * @param countVictoriesOnly
	 * @param subpops
	 * @param threadnum
	 */
	public void evaluateSubpop(final EvolutionState state,
			final MLSSubpopulation subpop,
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
			final Individual ind,
			final int subpopulation,
			final int threadnum);
}
