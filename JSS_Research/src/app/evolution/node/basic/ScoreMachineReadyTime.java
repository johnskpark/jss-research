package app.evolution.node.basic;

import app.node.NodeDefinition;
import app.evolution.JasimaGPData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ScoreMachineReadyTime extends GPNode {

	private static final long serialVersionUID = -3673528161888028806L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_MACHINE_READY_TIME.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_MACHINE_READY_TIME.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
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
