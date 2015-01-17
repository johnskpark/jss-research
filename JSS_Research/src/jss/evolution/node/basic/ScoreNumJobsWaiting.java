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

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class ScoreNumJobsWaiting extends GPNode {

	private static final long serialVersionUID = 3428306155083003334L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_NUM_JOBS_WAITING.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_NUM_JOBS_WAITING.numChildren()) {
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
		long startTime = System.nanoTime();

		JSSGPData data = (JSSGPData)input;

		data.setPriority(data.getMachine().getWaitingJobs().size());

		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;

		System.out.printf("Num jobs waiting: %d\n", timeDiff);
	}

}
