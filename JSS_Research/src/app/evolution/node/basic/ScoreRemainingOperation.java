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
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ScoreRemainingOperation extends SingleLineGPNode {

	private static final long serialVersionUID = -1499669041135595578L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_REMAINING_OPERATION;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JasimaGPData data = (JasimaGPData)input;

		data.setPriority(data.getPrioRuleTarget().numOpsLeft());
	}

}
