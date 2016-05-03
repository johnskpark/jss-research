package app.listener.nguyen_r1;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import app.IWorkStationListener;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

// TODO right, I need to test this to make sure that it works correctly.
public class NguyenR1Listener implements IWorkStationListener {

	private WorkloadStat[] stats;
	private int numMachines;

	// TPQ = total processing time in queue
	// TPG = total processing time remaining.

	private List<WorkloadStat> machinesByTPQ = new LinkedList<WorkloadStat>();
	private List<WorkloadStat> machinesByTPG = new LinkedList<WorkloadStat>();

	private int bneckIndex;
	private int cmIndex;

	public NguyenR1Listener() {
	}

	public int getNumMachines() {
		return numMachines;
	}

	public WorkloadStat getWorkloadStat(int index) {
		return stats[index];
	}

	public int getBneckIndex() {
		return bneckIndex;
	}

	public WorkloadStat getBneckWorkloadStat() {
		return stats[bneckIndex];
	}

	public int getCMIndex() {
		return cmIndex;
	}

	public WorkloadStat getCMWorkloadStat() {
		return stats[cmIndex];
	}

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

	// So in the paper, Omega prime is the workload is the
	// amount of workload in the current queue
	// I is the total workload remaining

	public void jobArrival(WorkStation machine) {
		int index = machine.index();

		PrioRuleTarget entry = machine.justArrived;
		for (int i = entry.getTaskNumber(); i < entry.numOps(); i++) {
			Operation op = entry.getOps()[i];

			stats[op.machine.index()].jobArrivalInShop(op);
		}

		stats[index].operationArrivalInQueue(entry, entry.getCurrentOperation());

		update();
	}

	public void operationComplete(WorkStation machine) {
		int index = machine.index();

		PrioRuleTarget entry = machine.justCompleted;
		stats[index].operationComplete(entry, entry.getCurrentOperation());

		update();
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

		machinesByTPQ.clear();
		machinesByTPG.clear();
		for (int i = 0; i < stats.length; i++) {
			machinesByTPQ.add(stats[i]);
			machinesByTPG.add(stats[i]);
		}

		bneckIndex = 0;
		cmIndex = 0;
	}

	private void update() {
		Collections.sort(machinesByTPQ, new TPQComparator());
		Collections.sort(machinesByTPG, new TPGComparator());

		bneckIndex = machinesByTPQ.get(0).getIndex();
		cmIndex = machinesByTPG.get(0).getIndex();
	}

	private class TPQComparator implements Comparator<WorkloadStat> {
		@Override
		public int compare(WorkloadStat o1, WorkloadStat o2) {
			if (o1.getTotalProcInQueue() > o2.getTotalProcInQueue()) {
				return -1;
			} else if (o1.getTotalProcInQueue() < o2.getTotalProcInQueue()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private class TPGComparator implements Comparator<WorkloadStat> {
		@Override
		public int compare(WorkloadStat o1, WorkloadStat o2) {
			if (o1.getTotalProcGlobal() > o2.getTotalProcGlobal()) {
				return -1;
			} else if (o1.getTotalProcGlobal() < o2.getTotalProcGlobal()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

}
