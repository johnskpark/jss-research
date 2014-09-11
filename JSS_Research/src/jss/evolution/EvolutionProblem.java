package jss.evolution;

import jss.JSSProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;

public class EvolutionProblem extends GPProblem implements JSSProblem {

	// This guy's job is to carry jss.Problem, insert the GP solution generator and fitness measures as solvers
	// to the jss.Problem, and then obtain the output statistics from it as evaluation measure.

	// I will make an interface called ... Problem maybe? I dunno.

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}
}
