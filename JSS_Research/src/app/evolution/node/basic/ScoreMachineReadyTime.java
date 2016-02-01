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
 * Terminal node which returns the value of the time when the machine becomes
 * available for an arithmetic function tree.
 *
 * @author parkjohn
 *
 */
public class ScoreMachineReadyTime extends SingleLineGPNode {

	private static final long serialVersionUID = -3673528161888028806L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_MACHINE_READY_TIME;

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

		data.setPriority(data.getPrioRuleTarget().getShop().simTime());
	}

}
