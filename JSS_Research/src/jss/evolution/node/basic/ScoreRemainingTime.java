package jss.evolution.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.IProblemInstance;
import jss.evolution.JSSData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreRemainingTime extends GPNode {

	private static final long serialVersionUID = 5176332159809663461L;

	@Override
	public String toString() {
		return "TP";
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 0) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSData data = (JSSData)input;

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
