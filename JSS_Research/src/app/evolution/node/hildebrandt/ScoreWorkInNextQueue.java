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
import jasima.shopSim.core.WorkStation;

public class ScoreWorkInNextQueue extends SingleLineGPNode {

	private static final long serialVersionUID = 8357230245031223412L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_WORK_IN_NEXT_QUEUE;

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

		int nextTask = entry.getTaskNumber() + 1;
		if (nextTask >= entry.numOps()) {
			data.setPriority(0);
		} else {
			WorkStation nextMachine = entry.getOps()[nextTask].machine;
			data.setPriority(nextMachine.workContent(false));
		}
	}

}
