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
 * Terminal node which returns a sufficiently large value for an arithmetic
 * function tree.
 *
 * @author parkjohn
 *
 */
public class ScoreLargeValue extends SingleLineGPNode {

	private static final long serialVersionUID = -5825676453528932050L;

	private static final double LARGE_VALUE = 100000000.0;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_LARGE_VALUE;

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

		data.setPriority(LARGE_VALUE);
	}

}
