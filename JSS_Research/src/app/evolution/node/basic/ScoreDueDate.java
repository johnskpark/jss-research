package app.evolution.node.basic;

import app.evolution.JasimaGPData;
import app.evolution.node.GPSingleLinePrintNode;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

/**
 * Terminal node which returns the value of the due date of a job for an arithmetic
 * function tree.
 *
 * @author parkjohn
 *
 */
public class ScoreDueDate extends GPSingleLinePrintNode {

	private static final long serialVersionUID = -4015393052275553333L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_DUE_DATE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_DUE_DATE.numChildren()) {
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
		JasimaGPData data = (JasimaGPData)input;

		data.setPriority(data.getPrioRuleTarget().getDueDate());
	}

}
