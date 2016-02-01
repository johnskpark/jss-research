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
 * Non-terminal operator representing the greater than or equal to zero (>= 0)
 * conditional operator in an arithmetic function tree.
 *
 * The (>= 0) conditional is a ternary operator, where the first argument
 * determine whether to return the value of the second argument or the third
 * argument. If the first argument is >= 0, then the second argument is returned
 * as a value. Otherwise, the third argument is returned.
 *
 * @author parkjohn
 *
 */
public class OpConditional extends SingleLineGPNode {

	private static final long serialVersionUID = -8055215086941685756L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_CONDITIONAL;

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
		double condPriority = data.getPriority();

		children[1].eval(state, thread, input, stack, individual, problem);
		double ifPriority = data.getPriority();

		children[2].eval(state, thread, input, stack, individual, problem);
		double elsePriority = data.getPriority();

		if (condPriority >= 0) {
			data.setPriority(ifPriority);
		} else {
			data.setPriority(elsePriority);
		}
	}

}
