package app.evolution.node.nguyen_r1;

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
import jasima.shopSim.core.PrioRuleTarget;

public class AttributeWorkloadRatio extends SingleLineGPNode {

	private static final long serialVersionUID = -1471090646930240672L;
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

		NguyenR1Listener listener = (NguyenR1Listener) data.getWorkStationListener(NguyenR1Listener.class.getSimpleName());

		PrioRuleTarget entry = data.getPrioRuleTarget();
		WorkloadStat machineStat = listener.getWorkloadStat(entry.getCurrMachine().index());

		data.setPriority(1.0 * machineStat.getTotalProcInQueue() / machineStat.getTotalProcGlobal());
	}

}
