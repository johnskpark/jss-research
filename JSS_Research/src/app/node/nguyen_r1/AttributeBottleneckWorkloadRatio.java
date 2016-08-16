package app.node.nguyen_r1;

import java.util.List;
import java.util.Map;

import app.IWorkStationListener;
import app.listener.nguyen_r1.NguyenR1Listener;
import app.listener.nguyen_r1.WorkloadStat;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;

@NodeAnnotation(node=NodeDefinition.ATTRIBUTE_BOTTLENECK_WORKLOAD_RATIO)
public class AttributeBottleneckWorkloadRatio implements INode {

	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_BOTTLENECK_WORKLOAD_RATIO;

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

		PrioRuleTarget entry = data.getPrioRuleTarget();
		WorkloadStat machineStat = listener.getWorkloadStat(entry.getCurrMachine().index());

		List<PrioRuleTarget> jobsInQueue = machineStat.getJobsInQueue();
		int bneckIndex = listener.getBneckIndex();

		double bneckProcTime = 0.0;

		for (PrioRuleTarget job : jobsInQueue) {
			for (int i = job.getTaskNumber() + 1; i < job.numOps(); i++) {
				Operation futureOp = job.getOps()[i];

				if (futureOp.machine.index() == bneckIndex) {
					bneckProcTime += job.currProcTime();
				}
			}
		}

		return 1.0 * bneckProcTime / machineStat.getTotalProcInQueue();
	}

}
