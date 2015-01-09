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

public class OpMaximum extends GPNode {

	private static final long serialVersionUID = 2561827321918468718L;

	@Override
	public String toString() {
		return NodeDefinition.OP_MAXIMUM.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.OP_MAXIMUM.numChildren()) {
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
		JSSGPData data = (JSSGPData)input;

		children[0].eval(state, thread, input, stack, individual, problem);
		double priority1 = data.getPriority();

		children[1].eval(state, thread, input, stack, individual, problem);
		double priority2 = data.getPriority();

		data.setPriority(Math.max(priority1, priority2));
	}

}
