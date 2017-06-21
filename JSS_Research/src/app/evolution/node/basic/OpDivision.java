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
 * Non-terminal node representing the protected division on an arithmetic
 * function tree.
 *
 * In protected division, the value of the nominator divided by the denominator is
 * returned if the denominator does not equal zero. Otherwise, it returns the value
 * 1.0.
 *
 * @author parkjohn
 *
 */
public class OpDivision extends SingleLineGPNode {

	private static final long serialVersionUID = 2589591919537774922L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.OP_DIVISION;
	private static final double MINIMUM_THRESHOLD_FROM_ZERO = 0.0000001;

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

		if (Math.abs(priority2) < MINIMUM_THRESHOLD_FROM_ZERO) {
			data.setPriority(1.0);
		} else {
			data.setPriority(priority1 / priority2);
		}
	}

}
