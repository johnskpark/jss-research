package app.evolution.node.hunt;

import java.util.Map;

import app.IWorkStationListener;
import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.listener.hunt.HuntListener;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

//The average wait time of last five jobs processed all machines on the shop floor.
public class ScoreAverageWaitTimeAllMachines extends SingleLineGPNode {

	private static final long serialVersionUID = -2890903764607495129L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.SCORE_AVERAGE_WAIT_TIME_ALL_MACHINE;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		JasimaGPData data = (JasimaGPData) input;

		Map<String, IWorkStationListener> listeners = data.getWorkStationListeners();
		HuntListener listener = (HuntListener) listeners.get(HuntListener.class.getSimpleName());

		data.setPriority(listener.getAverageWaitTimesAllMachines());
	}

}
