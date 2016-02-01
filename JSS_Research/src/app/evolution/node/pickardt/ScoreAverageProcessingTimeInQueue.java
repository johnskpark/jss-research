package app.evolution.node.pickardt;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class ScoreAverageProcessingTimeInQueue extends SingleLineGPNode {

	private static final long serialVersionUID = 5789849512075356407L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE;

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

		double sumProcTime = 0.0;

		PriorityQueue<?> q = entry.getCurrMachine().queue;
		for (int i = 0; i < q.size(); i++) {
			sumProcTime += q.get(i).currProcTime();
		}

		data.setPriority(sumProcTime / q.size());
	}

}
