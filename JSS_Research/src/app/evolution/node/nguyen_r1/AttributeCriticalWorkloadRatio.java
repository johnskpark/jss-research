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

public class AttributeCriticalWorkloadRatio extends SingleLineGPNode {

	private static final long serialVersionUID = -6615813483304327187L;
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

		data.setPriority(1.0 * cmProcTime / machineStat.getTotalProcInQueue());
	}

}
