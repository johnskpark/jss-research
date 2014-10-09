package jss.evolution.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.evolution.JSSGPData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreSetupTime extends GPNode {

	private static final long serialVersionUID = 5569072938030248389L;

	private static final int CHILDREN_NUM = 0;

	@Override
	public String toString() {
		return "S";
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

		IMachine machine = data.getMachine();
		IJob job = data.getJob();

		data.setPriority(job.getSetupTime(machine));
	}

}
