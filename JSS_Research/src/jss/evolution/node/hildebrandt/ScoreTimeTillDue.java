package jss.evolution.node.hildebrandt;

import jss.IJob;
import jss.evolution.JSSGPData;
import jss.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

public class ScoreTimeTillDue extends GPNode {

	private static final long serialVersionUID = -3554986796800922750L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_TIME_TILL_DUE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_TIME_TILL_DUE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JSSGPData data = (JSSGPData)input;

		IJob job = data.getJob();
		double time = data.getCurrentTime();

		data.setPriority(job.getDueDate() - time);
	}

}
