package jss.evolution.sample;

import java.util.List;

import jss.Action;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class TwoStaticJSSProblem extends GPProblem {

	private static final long serialVersionUID = 3L;

	private static final int DATASET_SEED = 15;

	private TwoStaticJSSDataset dataset = new TwoStaticJSSDataset(DATASET_SEED);
	private TwoStaticJSSSolver solver = new TwoStaticJSSSolver();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		input = (BasicData)state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, BasicData.class);
		input.setup(state, base.push(P_DATA));
	}

	// TODO need to clone stuff for later.

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!(ind instanceof GPIndividual)) {
			// TODO fatal error.
		}

		BasicStatistics stats = new BasicStatistics();

		solver.setRule(new BasicRule(state, (GPIndividual)ind, subpopulation, threadnum));

		for (TwoStaticJSSInstance problem : dataset.getProblems()) {
			List<Action> solutionActions = solver.getSolution(problem);
		}
	}

}
