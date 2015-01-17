package jss.evolution.node.basic;

import jss.evolution.JSSGPData;
import jss.node.NodeDefinition;
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
public class ScoreRemainingOperation extends GPNode {

	private static final long serialVersionUID = -1499669041135595578L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_REMAINING_OPERATION.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_REMAINING_OPERATION.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		long startTime = System.nanoTime();

		JSSGPData data = (JSSGPData)input;

		data.setPriority(data.getJob().getRemainingOperations());

		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;

		System.out.printf("Remaining operation: %d\n", timeDiff);
	}

}
