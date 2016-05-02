package app.evolution.node.nguyen_r1;

import java.util.Map;

import app.IWorkStationListener;
import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.listener.nguyen_r1.NguyenR1Listener;
import app.listener.nguyen_r1.WorkloadStat;
import app.node.NodeDefinition;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

public class AttributeDeviationOfJobs extends SingleLineGPNode {

	private static final long serialVersionUID = -4596511901710548976L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_CRITICAL_MACHINE_IDLENESS;

	@Override
	public String toString() {
		return NODE_DEFINITION.toString();
	}

	@Override
	public int expectedChildren() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {
		JasimaGPData data = (JasimaGPData) input;

		Map<String, IWorkStationListener> listeners = data.getWorkStationListeners();
		NguyenR1Listener listener = (NguyenR1Listener) listeners.get(NguyenR1Listener.class.getSimpleName());

		double minOpProcTime = Double.POSITIVE_INFINITY;
		double maxOpProcTime = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < listener.getNumMachines(); i++) {
			WorkloadStat stat = listener.getWorkloadStat(i);

			minOpProcTime = Math.min(minOpProcTime, stat.getMinWorkload());
			maxOpProcTime = Math.max(maxOpProcTime, stat.getMaxWorkload());
		}

		data.setPriority(1.0 * minOpProcTime / maxOpProcTime);
	}

}
