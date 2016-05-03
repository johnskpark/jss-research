package app.evolution.node.nguyen_r1;

import java.util.List;
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
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;

public class AttributeBottleneckWorkloadRatio extends SingleLineGPNode {

	private static final long serialVersionUID = 4135569476364255376L;
	private static final NodeDefinition NODE_DEFINITION = NodeDefinition.ATTRIBUTE_BOTTLENECK_WORKLOAD_RATIO;

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

		data.setPriority(1.0 * bneckProcTime / machineStat.getTotalProcInQueue());
	}

}
