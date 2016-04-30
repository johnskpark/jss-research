package app.listener.nguyen_r1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import app.IWorkStationListener;
import jasima.shopSim.core.Operation;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

// TODO right, I need to test this to make sure that it works correctly.
public class NguyenR1Listener implements IWorkStationListener {

	private WorkloadStat[] stats;
	private int numMachines;

	private List<WorkloadStat> machinesByWorkload = new LinkedList<WorkloadStat>();
	private Map<Integer, Integer> indexToPos = new HashMap<Integer, Integer>();
	private double bneckTotalProcInQueue;
	private int bneckIndex;

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

		stats[index].operationArrivalInQueue(entry.getCurrentOperation());

		if (stats[index].getTotalProcInQueue() > bneckTotalProcInQueue) {
			bneckTotalProcInQueue = stats[index].getTotalProcInQueue();
			bneckIndex = index;
		}

		updateBottleneck(machine, true);
	}

	public void operationComplete(WorkStation machine) {
		int index = machine.index();

		PrioRuleTarget entry = machine.justCompleted;
		stats[index].operationComplete(entry.getCurrentOperation());

		updateBottleneck(machine, false);
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

		machinesByWorkload.clear();
		for (int i = 0; i < stats.length; i++) {
			machinesByWorkload.add(stats[i]);
			indexToPos.put(i, i);
		}

		bneckTotalProcInQueue = 0.0;
		bneckIndex = 0;
	}

	private void updateBottleneck(WorkStation machine, boolean workloadIncreased) {
		int pos = indexToPos.get(machine.index());

		if (workloadIncreased && pos != 0) {
			WorkloadStat stat = machinesByWorkload.remove(pos);

			int newPos = pos - 1;
			while (newPos >= 0 && machinesByWorkload.get(newPos).getTotalProcInQueue() < stat.getTotalProcInQueue()) {
				newPos--;
			}

			newPos++;
			machinesByWorkload.add(newPos, stat);
			indexToPos.put(machine.index(), newPos);
		} else if (pos != machinesByWorkload.size() - 1){
			WorkloadStat stat = machinesByWorkload.remove(pos);

			int newPos = pos + 1;
			while (newPos < machinesByWorkload.size() && machinesByWorkload.get(newPos).getTotalProcInQueue() > stat.getTotalProcInQueue()) {
				newPos++;
			}

			newPos--;
			machinesByWorkload.add(newPos, stat);
			indexToPos.put(machine.index(), newPos);
		}

		WorkloadStat bneckStat = machinesByWorkload.get(0);
		bneckTotalProcInQueue = bneckStat.getTotalProcInQueue();
		bneckIndex = bneckStat.getIndex();
	}

}
