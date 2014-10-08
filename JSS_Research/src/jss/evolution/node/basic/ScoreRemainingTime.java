package jss.evolution.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.JSSGPData;
import jss.evolution.node.INode;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreRemainingTime extends GPNode implements INode {

	private static final long serialVersionUID = 5176332159809663461L;

	private static final int CHILDREN_NUM = 0;
	
	@Override
	public String toString() {
		return "TP";
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
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSGPData data = (JSSGPData)input;

		IProblemInstance problemInstance = data.getProblem();
		IJob job = data.getJob();

		double remainingTime = 0;
		for (IMachine machine : problemInstance.getMachines()) {
			if (job.isProcessable(machine)) {
				remainingTime += job.getProcessingTime(machine);
			}
		}

		data.setPriority(remainingTime);
	}

}
