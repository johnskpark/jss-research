package app.evolution.node.basic;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

/**
 * Non-terminal node representing the maximum operator in an arithmetic function tree,
 * where the maximum of the two arguments is returned.
 *
 * @author parkjohn
 *
 */
public class OpMaximum extends SingleLineGPNode {

	private static final long serialVersionUID = 2561827321918468718L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_MAXIMUM;

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
		double priority1 = data.getPriority();

		children[1].eval(state, thread, input, stack, individual, problem);
		double priority2 = data.getPriority();

		data.setPriority(Math.max(priority1, priority2));
	}

}
