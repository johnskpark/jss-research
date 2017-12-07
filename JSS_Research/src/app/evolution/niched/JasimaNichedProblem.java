package app.evolution.niched;

import app.evolution.simple.JasimaSimpleProblem;
import ec.EvolutionState;

public class JasimaNichedProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -3573529649173003108L;

	// TODO the code here.

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		// TODO insert the simconfig here into the fitness?
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);

		// TODO do the archive update here.
	}

}
