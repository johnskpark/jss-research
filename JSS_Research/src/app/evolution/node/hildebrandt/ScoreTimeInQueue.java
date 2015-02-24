package app.evolution.node.hildebrandt;

import jasima.shopSim.core.PrioRuleTarget;
import app.evolution.JasimaGPData;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreTimeInQueue extends GPNode {

	private static final long serialVersionUID = 3254592684439750411L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_TIME_IN_QUEUE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_TIME_IN_QUEUE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JasimaGPData data = (JasimaGPData)input;

		PrioRuleTarget entry = data.getPrioRuleTarget();

		data.setPriority(Math.max(entry.getShop().simTime() - entry.getArriveTime(), 0));
	}

}
