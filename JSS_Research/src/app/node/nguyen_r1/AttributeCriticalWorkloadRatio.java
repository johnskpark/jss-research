package app.node.nguyen_r1;

import java.util.List;
import java.util.Map;

import app.JasimaWorkStationListener;
import app.listener.nguyen_r1.NguyenR1Listener;
import app.listener.nguyen_r1.WorkloadStat;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;

@NodeAnnotation(node=NodeDefinition.ATTRIBUTE_CRITICAL_WORKLOAD_RATIO)
public class AttributeCriticalWorkloadRatio implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_CRITICAL_WORKLOAD_RATIO;

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

		List<PrioRuleTarget> jobsInQueue = machineStat.getJobsInQueue();
		int cmIndex = 0; // critical machine index

		for (int i = 1; i < listener.getNumMachines(); i++) {
			WorkloadStat stat = listener.getWorkloadStat(i);

			if (stat.getTotalProcGlobal() > listener.getWorkloadStat(cmIndex).getTotalProcGlobal()) {
				cmIndex = i;
			}
		}

		double cmProcTime = 0.0;

		for (PrioRuleTarget job : jobsInQueue) {
			for (int i = job.getTaskNumber() + 1; i < job.numOps(); i++) {
				Operation futureOp = job.getOps()[i];

				if (futureOp.machine.index() == cmIndex) {
					cmProcTime += job.currProcTime();
				}
			}
		}

		return 1.0 * cmProcTime / machineStat.getTotalProcInQueue();
	}

}
