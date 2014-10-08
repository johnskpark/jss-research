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

public class ScoreLargeValue extends GPNode implements INode {

	private static final long serialVersionUID = -5825676453528932050L;

	private static final double LARGE_VALUE = 100000000.0;
	private static final int CHILDREN_NUM = 0;
	
	@Override
	public String toString() {
		return "Inf";
	}
	
	@Override
	public int getChildrenNum() {
		return CHILDREN_NUM;
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != CHILDREN_NUM) {
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
		data.setPriority(LARGE_VALUE);
	}

}
