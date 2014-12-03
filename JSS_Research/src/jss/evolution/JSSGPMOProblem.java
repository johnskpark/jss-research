package jss.evolution;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

// TODO the MO ensemble approach.
public class JSSGPMOProblem extends GPProblem {

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		// TODO Auto-generated method stub
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {

		}
	}

}
