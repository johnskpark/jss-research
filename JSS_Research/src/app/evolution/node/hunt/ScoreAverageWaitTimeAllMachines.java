package app.evolution.node.hunt;

import app.evolution.JasimaGPData;
import app.evolution.node.GPSingleLinePrintNode;
import app.listener.hunt.HuntListener;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

//The average wait time of last five jobs processed all machines on the shop floor.
public class ScoreAverageWaitTimeAllMachines extends GPSingleLinePrintNode {

	private static final long serialVersionUID = -2890903764607495129L;

	@Override
	public String toString() {
		return NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE.toString();
	}

	@Override
	public void checkConstraints(final EvolutionState state,
			final int tree,
			final GPIndividual typicalIndividual,
			final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE.numChildren()) {
			state.output.error("Incorrect number of children for node " + toStringForError() + " at " + individualBase);
		}
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JasimaGPData data = (JasimaGPData) input;
		HuntListener listener = (HuntListener) data.getWorkStationListener();

		data.setPriority(listener.getAverageWaitTimesAllMachines());
	}

}
