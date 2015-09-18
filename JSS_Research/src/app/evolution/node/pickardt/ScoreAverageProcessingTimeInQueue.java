package app.evolution.node.pickardt;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import app.evolution.JasimaGPData;
import app.evolution.node.GPSingleLinePrintNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class ScoreAverageProcessingTimeInQueue extends GPSingleLinePrintNode {

	private static final long serialVersionUID = 5789849512075356407L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_NEXT_PROCESSING_TIME.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_AVERAGE_PROCESSING_TIME_IN_QUEUE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
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
