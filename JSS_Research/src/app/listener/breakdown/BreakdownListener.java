package app.listener.breakdown;

import app.IWorkStationListener;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public class BreakdownListener implements IWorkStationListener {

	private SummaryStat[] breakdownTimePerMachine;
	private SummaryStat[] repairTimePerMachine;
	private SummaryStat[] upTimePerMachine;

	private SummaryStat breakdownTimeAllMachines;
	private SummaryStat repairTimeAllMachines;
	private SummaryStat upTimeAllMachines;

	private BreakdownStartStat[] brokenDownMachines;

	private boolean[] previouslyDeactivated;
	private boolean previouslyDeactivatedAny;
	private int numMachines;

	public BreakdownListener() {
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_DEACTIVATED) {
			machineDeactivated(notifier);
		} else if (event == WorkStation.WS_ACTIVATED) {
			machineActivated(notifier);
		} else if (event == WorkStation.WS_INIT) {
			init(notifier);
		}
	}

	public void machineDeactivated(WorkStation machine) {
		int index = machine.index();

		if (brokenDownMachines[index] != null) {
			throw new RuntimeException("The machine should not currently be broken down.");
		}

		BreakdownStartStat stat = new BreakdownStartStat();
		double breakdownTime = machine.shop().simTime();

		stat.machine = machine;
		stat.breakdownTime = breakdownTime;

		brokenDownMachines[index] = stat;

		if (previouslyDeactivated[index]) {
			double upTime = breakdownTime - breakdownTimePerMachine[index].lastValue() - repairTimePerMachine[index].lastValue();
			upTimePerMachine[index].value(upTime);
			upTimeAllMachines.value(upTime);
		} else {
			upTimePerMachine[index].value(breakdownTime);
			upTimeAllMachines.value(breakdownTime);

			previouslyDeactivated[index] = true;
		}

		breakdownTimePerMachine[index].value(stat.breakdownTime);
		breakdownTimeAllMachines.value(stat.breakdownTime);

		previouslyDeactivatedAny = true;
	}

	public void machineActivated(WorkStation machine) {
		int index = machine.index();
		if (!previouslyDeactivated[index]) {
			return;
		}

		if (brokenDownMachines[index].machine != machine) {
			throw new RuntimeException("The machine previously recorded to be broken down does not match the current activated machine.");
		}

		BreakdownStartStat startStat = brokenDownMachines[index];
		double breakdownTime = startStat.breakdownTime;
		double repairTime = machine.shop().simTime() - breakdownTime;

		repairTimePerMachine[index].value(repairTime);
		repairTimeAllMachines.value(repairTime);

		brokenDownMachines[index] = null;
	}

	public void init(WorkStation machine) {
		numMachines = machine.shop().machines.length;

		clear();
	}

	public boolean hasBrokenDown(WorkStation machine) {
		return previouslyDeactivated[machine.index()];
	}

	public SummaryStat getMachineBreakdownStat(WorkStation machine) {
		return breakdownTimePerMachine[machine.index()];
	}

	public SummaryStat getMachineRepairTimeStat(WorkStation machine) {
		return repairTimePerMachine[machine.index()];
	}

	public SummaryStat getMachineUpTimeStat(WorkStation machine) {
		return upTimePerMachine[machine.index()];
	}

	public boolean hasBrokenDownAnyMachine() {
		return previouslyDeactivatedAny;
	}

	public SummaryStat getAllMachineBreakdownStat() {
		return breakdownTimeAllMachines;
	}

	public SummaryStat getAllMachineRepairStat() {
		return repairTimeAllMachines;
	}

	public SummaryStat getAllMachineUpTimeStat() {
		return upTimeAllMachines;
	}

	@Override
	public void clear() {
		breakdownTimePerMachine = new SummaryStat[numMachines];
		repairTimePerMachine = new SummaryStat[numMachines];
		upTimePerMachine = new SummaryStat[numMachines];

		breakdownTimeAllMachines = new SummaryStat();
		repairTimeAllMachines = new SummaryStat();
		upTimeAllMachines = new SummaryStat();

		brokenDownMachines = new BreakdownStartStat[numMachines];

		previouslyDeactivated = new boolean[numMachines];
		previouslyDeactivatedAny = false;

		for (int i = 0; i < numMachines; i++) {
			breakdownTimePerMachine[i] = new SummaryStat();
			repairTimePerMachine[i] = new SummaryStat();
			upTimePerMachine[i] = new SummaryStat();

			previouslyDeactivated[i] = false;
		}
	}

	private class BreakdownStartStat {
		WorkStation machine;

		double breakdownTime;
	}

}
