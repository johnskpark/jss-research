package jss.evolution.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.evolution.sample.BasicData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreProcessingTime extends GPNode {

	private static final long serialVersionUID = 4917340755318117709L;

	@Override
	public String toString() {
		return "P";
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
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		BasicData data = (BasicData)input;

		IMachine machine = data.getMachine();
		IJob job = data.getJob();

		data.setPriority(job.getProcessingTime(machine));
	}

}
