package app.evolution.node.hunt;

import jasima.shopSim.core.PrioRuleTarget;
import app.evolution.JasimaGPData;
import app.evolution.node.GPSingleLinePrintNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class ScoreNextProcessingTime extends GPSingleLinePrintNode	 {

	private static final long serialVersionUID = 6065575150331988114L;

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
		if (children.length != NodeDefinition.SCORE_NEXT_PROCESSING_TIME.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
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
			data.setPriority(entry.getOps()[nextTask].procTime);
		}
	}

}
