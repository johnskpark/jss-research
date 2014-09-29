package jss.evolution;

import jss.ISolver;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JSSProblem extends GPProblem {

	// TODO The fitness measure, the rule converter and the problem dataset
	ISolver solver;

	@Override
	public void setup(EvolutionState state, Parameter parameter) {
		super.setup(state, parameter);


	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
