package app.evolution.multilevel;

import ec.Evaluator;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

// TODO right, I need a selection evaluator and a breeder perhaps?
public class MLSEvaluator extends Evaluator {

    // the preamble for selecting partners from each subpopulation
    public static final String P_SUBPOP = "subpop";

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// TODO check what sequential breeder means.
		// Check the type of the breeders, which are set up after the evaluators.
		if (state.breeder instanceof MLSBreeder && ((MLSBreeder) state.breeder).isSequentialBreeding()) {
			state.output.message("The Breeder is breeding sequentially, so the MultilevelSelectionEvaluator is also evaluating sequentially.");
		}


        // at this point, we do not know the number of subpopulations, so we read it as well from the parameters file
        Parameter tempSubpop = new Parameter(ec.Initializer.P_POP).push(ec.Population.P_SIZE);
        int numSubpopulations = state.parameters.getInt(tempSubpop, null, 0);
        if (numSubpopulations <= 1) {
            state.output.fatal("There must be at least 2 subpopulations for MultilevelSelectionEvaluator", tempSubpop);
        }

        // TODO more parameters checks here.
	}

	@Override
	public void evaluatePopulation(final EvolutionState state) {
		// TODO Auto-generated method stub

		// Right, how will this be done?
	}

	public void evaluateSubpopulation(final EvolutionState state, MLSSubpopulation subpop) {
		// TODO Auto-generated method stub.
	}

	public void evaluateIndividual(final EvolutionState state, Individual ind) {
		// TODO Auto-generated method stub.
	}

	@Override
	public boolean runComplete(final EvolutionState state) {
		return false;
	}

}