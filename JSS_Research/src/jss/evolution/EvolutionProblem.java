package jss.evolution;

import jss.JSSProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class EvolutionProblem extends GPProblem implements JSSProblem {




	@Override
	public void setup(EvolutionState state, Parameter parameter) {
		super.setup(state, parameter);

		// TODO Auto-generated method stub.

		loadConfig(parameter);
	}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}

	private void loadConfig(Parameter parameter) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
