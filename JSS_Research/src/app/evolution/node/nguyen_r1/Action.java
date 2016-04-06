package app.evolution.node.nguyen_r1;

import app.evolution.node.SingleLineGPNode;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

public class Action extends SingleLineGPNode {

	private static final long serialVersionUID = 81315963399640321L;

	@Override
	public String toString() {
		return "action";
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		children[0].eval(state, thread, input, stack, individual, problem);
	}

}
