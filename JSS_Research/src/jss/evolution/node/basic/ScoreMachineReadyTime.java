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
		long startTime = System.nanoTime();

		JSSGPData data = (JSSGPData)input;

		data.setPriority(data.getMachine().getReadyTime());

		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;

		System.out.printf("Machine ready: %d\n", timeDiff);
	}

}
