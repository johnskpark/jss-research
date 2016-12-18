package app.node.nguyen_r1;

import java.util.Map;

import app.JasimaWorkStationListener;
import app.listener.nguyen_r1.NguyenR1Listener;
import app.listener.nguyen_r1.WorkloadStat;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.PrioRuleTarget;

@NodeAnnotation(node=NodeDefinition.ATTRIBUTE_MACHINE_PROGRESS)
public class AttributeMachineProgress implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_MACHINE_PROGRESS;

	@Override
	public int getChildrenNum() {
		return NODE_DEFINITION.numChildren();
	}

	@Override
	public int getSize() {
		return NODE_DEFINITION.numChildren() + 1;
	}

	@Override
	public double evaluate(NodeData data) {
		Map<String, JasimaWorkStationListener> listeners = data.getWorkStationListeners();
		NguyenR1Listener listener = (NguyenR1Listener) listeners.get(NguyenR1Listener.class.getSimpleName());

		PrioRuleTarget entry = data.getPrioRuleTarget();
		WorkloadStat machineStat = listener.getWorkloadStat(entry.getCurrMachine().index());

		return 1.0 * machineStat.getSumCompletedProcTime() /
				(machineStat.getTotalProcGlobal() + machineStat.getSumCompletedProcTime());
	}

}
