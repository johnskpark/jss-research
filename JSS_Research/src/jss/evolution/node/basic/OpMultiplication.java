package jss.evolution.node.basic;

import jss.evolution.JSSGPData;
import jss.evolution.node.INode;
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
public class OpMultiplication extends GPNode implements INode {

	private static final long serialVersionUID = 672174749690633859L;

	private static final int NUM_CHILDREN = 2;
	
	@Override
	public String toString() {
		return "*";
	}
	
	@Override
	public int getChildrenNum() {
		return NUM_CHILDREN;
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

		data.setPriority(priority1 * priority2);
	}

}
