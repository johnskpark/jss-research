package jss.evolution.sample;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class TwoStaticJSSProblem extends GPProblem {

	private static final long serialVersionUID = 1L;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		input = (TwoStaticJSSData)state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, TwoStaticJSSData.class);
		input.setup(state, base.push(P_DATA));
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// TODO right...

	}

}
