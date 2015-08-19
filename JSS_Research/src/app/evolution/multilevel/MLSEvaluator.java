package app.evolution.multilevel;

import java.util.Arrays;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.coevolve.GroupedProblemForm;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;

// TODO

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
        if (numSubpopulations <= 1) {
            state.output.fatal("There must be at least 2 subpopulations for MultilevelSelectionEvaluator", tempSubpop);
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
		for(int i = 0; i < state.population.subpops.length; i++) {
			postAssessFitness[i] = shouldEvaluateSubpop(state, i, 0);
			preAssessFitness[i] = postAssessFitness[i] || (state.generation == 0);  // always prepare (set up trials) on generation 0
		}

		// Should I be using the grouped problem form for this?
		MLSProblemForm mlsProblem = (MLSProblemForm) p_problem;
		mlsProblem.preprocessPopulation(state, state.population, preAssessFitness, false);

		for (int subpop = 0; subpop < state.population.subpops.length; subpop++) {
			evaluateSubpopulation(state, (MLSSubpopulation) state.population.subpops[subpop], subpop);
		}

		mlsProblem.postprocessPopulation(state, state.population, postAssessFitness, false);
	}

	/**
	 * TODO javadoc.
	 * @param state
	 * @param subpop
	 * @param subpopIndex
	 */
	public void evaluateSubpopulation(final EvolutionState state, MLSSubpopulation subpop, int subpopIndex) {
		MLSProblemForm mlsProblem = (MLSProblemForm) p_problem;

		// By default, we update the fitnesses of all individual in the subpopulation.
		boolean[] updates = new boolean[subpop.individuals.length];
		Arrays.fill(updates, true);

		int[] indices = new int[subpop.individuals.length];
		Arrays.fill(indices, subpopIndex);

		// Evaluate the subpopulation of individuals.
		mlsProblem.evaluateSubpop(state, subpop, updates, false, indices, 0);
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

		mlsProblem.evaluateInd(state, ind, subpopIndex, 0);
	}

}
