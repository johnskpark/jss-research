package jss.evolution;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;

// Add the stupid shit in here.
public class JSSGPTestStatistics extends SimpleStatistics {

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		JSSGPTestProblem problem = (JSSGPTestProblem) state.evaluator.p_problem;
	}

}
