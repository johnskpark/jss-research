package app.evolution.node.hildebrandt;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;

public class ScoreSlack extends SingleLineGPNode {

	private static final long serialVersionUID = -1707060076476871895L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_SLACK;

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

		PrioRuleTarget entry = data.getPrioRuleTarget();

		data.setPriority(Math.max(entry.getDueDate() - entry.getShop().simTime() - entry.remainingProcTime(), 0));
	}

}
