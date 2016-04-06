package app.evolution.node.nguyen_r1;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

public class OpGreaterThan extends SingleLineGPNode {

	private static final long serialVersionUID = 1262511176528581813L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_GREATER_THAN;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData)input;

		children[0].eval(state, thread, input, stack, individual, problem);
		double attribute = data.getPriority();

		children[1].eval(state, thread, input, stack, individual, problem);
		double threshold = data.getPriority();

		if (attribute > threshold) {
			data.setPriority(-1.0);
		} else {
			data.setPriority(1.0);
		}
	}

}
