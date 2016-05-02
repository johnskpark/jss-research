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

public class AttributeCriticalMachineIdleness extends SingleLineGPNode {

	private static final long serialVersionUID = -1803406643776195193L;
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

		int cmi = 0; // critical machine index
		for (int i = 1; i < listener.getNumMachines(); i++) {
			WorkloadStat stat = listener.getWorkloadStat(i);

			if (stat.getTotalProcGlobal() > listener.getWorkloadStat(cmi).getTotalProcGlobal()) {
				cmi = i;
			}
		}

		WorkloadStat cmiStat = listener.getWorkloadStat(cmi);

		data.setPriority(1.0 * cmiStat.getTotalProcInQueue() / cmiStat.getTotalProcGlobal());
	}

}
