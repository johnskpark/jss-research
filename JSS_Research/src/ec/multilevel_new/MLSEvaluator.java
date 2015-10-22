package ec.multilevel_new;

import java.util.Arrays;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */

public class MLSEvaluator extends Evaluator {

	private static final long serialVersionUID = 4348544338928828593L;

	// The preamble for selecting partners from each subpopulation.
    public static final String P_SUBPOP = "subpop";

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Check the type of the breeders, which are set up after the evaluators.
		if (state.breeder instanceof MLSBreeder && ((MLSBreeder) state.breeder).isSequentialBreeding()) {
			state.output.message("The Breeder is breeding sequentially, so the MultilevelSelectionEvaluator is also evaluating sequentially.");
		}

        // At this point, we do not know the number of subpopulations, so we read it as well from the parameters file.
        Parameter tempSubpop = new Parameter(ec.Initializer.P_POP).push(ec.Population.P_SIZE);
        int numSubpopulations = state.parameters.getInt(tempSubpop, null, 0);
        if (numSubpopulations <= 0) {
            state.output.fatal("There must be at least one subpopulation for MultilevelSelectionEvaluator", tempSubpop);
        }
	}

	@Override
	public boolean runComplete(final EvolutionState state) {
		return false;
	}

    /**
     * Returns true if the subpopulation should be evaluated. This will happen if the
     * Breeder believes that the subpopulation should be breed afterwards.
     */
	public boolean shouldEvaluateSubpop(EvolutionState state, int subpop, int threadnum) {
		if (state.breeder instanceof MLSBreeder) {
			return ((MLSBreeder) state.breeder).shouldBreedSubpop(state, subpop, threadnum);
		} else {
			return false;
		}
	}

	@Override
	public void evaluatePopulation(final EvolutionState state) {
		// Determine who needs to be evaluated.
		boolean[] preAssessFitness = new boolean[state.population.subpops.length];
		boolean[] postAssessFitness = new boolean[state.population.subpops.length];
		for(int i = 1; i < state.population.subpops.length; i++) {
			postAssessFitness[i] = shouldEvaluateSubpop(state, i, 0);
			preAssessFitness[i] = postAssessFitness[i] || (state.generation == 0);  // always prepare (set up trials) on generation 0
		}

		((MLSProblemForm) p_problem).beforeEvaluation(state, state.population);

		MLSSubpopulation pop = (MLSSubpopulation) state.population.subpops[0];
		for (int ind = 0; ind < pop.individuals.length; ind++) {
			evaluateIndividual(state, pop, 0, pop.individuals[ind]);
		}

		for (int group = 1; group < state.population.subpops.length; group++) {
			evaluateGroup(state, (MLSSubpopulation) state.population.subpops[group], group);
		}
	}

	/**
	 * TODO javadoc.
	 * @param state
	 * @param subpop
	 * @param subpopIndex
	 */
	public void evaluateGroup(final EvolutionState state, MLSSubpopulation subpop, int subpopIndex) {
		MLSProblemForm mlsProblem = (MLSProblemForm) p_problem;

		// By default, we update the fitnesses of all individual in the subpopulation.
		boolean[] updates = new boolean[subpop.individuals.length];
		Arrays.fill(updates, true);

		int[] indices = new int[subpop.individuals.length];
		Arrays.fill(indices, subpopIndex);

		// Evaluate the subpopulation of individuals, which also includes null individuals.
		mlsProblem.evaluateGroup(state, subpop, updates, false, indices, 0);
	}

	/**
	 * TODO javadoc.
	 * @param state
	 * @param subpop
	 * @param subpopIndex
	 * @param ind
	 */
	public void evaluateIndividual(final EvolutionState state,
			MLSSubpopulation subpop,
			int subpopIndex,
			Individual ind) {
		MLSProblemForm mlsProblem = (MLSProblemForm) p_problem;

		// Evaluate the individual.
		mlsProblem.evaluateInd(state, ind, subpopIndex, 0);
	}

}
