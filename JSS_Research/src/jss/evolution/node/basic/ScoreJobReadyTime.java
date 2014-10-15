package jss.evolution.node.basic;

import jss.IJob;
import jss.IMachine;
import jss.evolution.JSSGPData;
import jss.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreJobReadyTime extends GPNode {

	private static final long serialVersionUID = -1153623043250724347L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_JOB_READY_TIME.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_JOB_READY_TIME.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}

		// TODO check to make sure that the job is static.
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JSSGPData data = (JSSGPData)input;

		IMachine machine = data.getMachine();
		IJob job = data.getJob();

		data.setPriority(job.getReadyTime(machine));
	}

}
