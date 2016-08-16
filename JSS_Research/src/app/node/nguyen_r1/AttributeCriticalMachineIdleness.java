package app.node.nguyen_r1;

import java.util.Map;

import app.IWorkStationListener;
import app.listener.nguyen_r1.NguyenR1Listener;
import app.listener.nguyen_r1.WorkloadStat;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;

@NodeAnnotation(node=NodeDefinition.ATTRIBUTE_CRITICAL_MACHINE_IDLENESS)
public class AttributeCriticalMachineIdleness implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_CRITICAL_MACHINE_IDLENESS;

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

		return 1.0 * cmiStat.getTotalProcInQueue() / cmiStat.getTotalProcGlobal();
	}

}
