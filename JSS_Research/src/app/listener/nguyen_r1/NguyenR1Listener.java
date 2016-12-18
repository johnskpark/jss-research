package app.listener.nguyen_r1;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import app.JasimaWorkStationListener;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

// TODO right, I need to test this to make sure that it works correctly.
public class NguyenR1Listener extends JasimaWorkStationListener {

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

	// So in the paper, Omega prime is the workload is the
	// amount of workload in the current queue
	// I is the total workload remaining

	@Override
	protected void init(WorkStation m) {
		numMachines = m.shop().machines.length;

		clear();
	}

	@Override
	protected void operationCompleted(WorkStation m, PrioRuleTarget justCompleted) {
		int index = m.index();

		stats[index].operationComplete(justCompleted, justCompleted.getCurrentOperation());

		update();
	}

	@Override
	protected void arrival(WorkStation m, Job justArrived) {
		int index = m.index();

		for (int i = justArrived.getTaskNumber(); i < justArrived.numOps(); i++) {
			Operation op = justArrived.getOps()[i];

			stats[op.machine.index()].jobArrivalInShop(op);
		}

		stats[index].operationArrivalInQueue(justArrived, justArrived.getCurrentOperation());

		update();
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
