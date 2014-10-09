package jss.evolution.node.basic;

import jss.evolution.JSSGPData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class OpConditional extends GPNode {

	private static final long serialVersionUID = -8055215086941685756L;

	private static final int NUM_CHILDREN = 3;

	@Override
	public String toString() {
		return "If";
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NUM_CHILDREN) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSGPData data = (JSSGPData)input;

		children[0].eval(state, thread, input, stack, individual, problem);
		double condPriority = data.getPriority();

		children[1].eval(state, thread, input, stack, individual, problem);
		double ifPriority = data.getPriority();

		children[2].eval(state, thread, input, stack, individual, problem);
		double elsePriority = data.getPriority();

		if (condPriority >= 0) {
			data.setPriority(ifPriority);
		} else {
			data.setPriority(elsePriority);
		}
	}

}
