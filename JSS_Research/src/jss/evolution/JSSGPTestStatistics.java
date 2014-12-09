package jss.evolution;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;

// Add the stupid shit in here
public class JSSGPTestStatistics extends SimpleStatistics {

	private static final long serialVersionUID = -9158971059437045071L;

	@Override
	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);

		JSSGPTestProblem problem = (JSSGPTestProblem) state.evaluator.p_problem;

		// Print out the best ensemble over the specific generation that was evaluated.
		state.output.println("Ensemble:", statisticslog);
		
		GPIndividual[] inds = problem.getBestGroupOfGeneration();
		for (int i = 0; i < inds.length; i++) {
			inds[i].printIndividualForHumans(state, statisticslog);
		}
	}
	
	@Override
	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);
		
		JSSGPTestProblem problem = (JSSGPTestProblem) state.evaluator.p_problem;
		
		// Print out the best ensemble over all generation that was evaluated.
		state.output.println("Ensemble:", statisticslog);
		
		GPIndividual[] inds = problem.getBestGroupOfGeneration();
		for (int i = 0; i < inds.length; i++) {
			inds[i].printIndividualForHumans(state, statisticslog);
		}
	}

}
