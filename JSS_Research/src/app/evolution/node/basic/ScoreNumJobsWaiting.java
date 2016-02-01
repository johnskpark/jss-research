package app.evolution.node.basic;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

/**
 * Terminal node which returns the value of the number of jobs waiting at the machine
 * for an arithmetic function tree.
 *
 * @author parkjohn
 *
 */
public class ScoreNumJobsWaiting extends SingleLineGPNode {

	private static final long serialVersionUID = -2790466031950356470L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_NUM_JOBS_WAITING;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(final EvolutionState state,
			final int thread,
			final GPData input,
			final ADFStack stack,
			final GPIndividual individual,
			final Problem problem) {
		JasimaGPData data = (JasimaGPData)input;

		data.setPriority(data.getPrioRuleTarget().getCurrMachine().numJobsWaiting());
	}

}
