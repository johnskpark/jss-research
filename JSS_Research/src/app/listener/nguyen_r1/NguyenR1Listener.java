package app.listener.nguyen_r1;

import app.IWorkStationListener;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public class NguyenR1Listener implements IWorkStationListener {

	private WorkloadStat[] stats;
	private int numMachines;

	private int bottleneckIndex;

	public NguyenR1Listener(int numMachines) {
		this.numMachines = numMachines;
		this.stats = new WorkloadStat[numMachines];
	}

	// TODO I will need to update the workload when the jobs arrive as well.

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_JOB_ARRIVAL) {
			jobArrival(notifier);
		} else if (event == WorkStation.WS_JOB_COMPLETED){
			operationComplete(notifier);
		} else if (event == WorkStation.WS_INIT) {
			init(notifier);
		}
	}

	// So in the paper, Omega prime is the workload is the amount of workload in
	// the current queue
	// I is the total workload remaining

	public void jobArrival(WorkStation machine) {
		int index = machine.index();

		PrioRuleTarget entry = machine.justArrived;
		for (int i = entry.getTaskNumber(); i < entry.numOps(); i++) {
			Operation op = entry.getOps()[i];

			stats[op.machine.index()].jobArrivalInShop(op);
		}

		stats[index].operationArrivalInQueue(entry.getCurrentOperation());

//		TODO find the bottleneck machine?
	}

	public void operationComplete(WorkStation machine) {
		int index = machine.index();

		PrioRuleTarget entry = machine.justCompleted;
		stats[index].operationComplete(entry.getCurrentOperation());

//		TODO find the bottleneck machine?
	}

	public void init(WorkStation machine) {
		numMachines = machine.shop().machines.length;

		clear();
	}

	@Override
	public void clear() {
		stats = new WorkloadStat[numMachines];
		for (int i = 0; i < numMachines; i++) {
			stats[i] = new WorkloadStat(i);
		}
	}

}
