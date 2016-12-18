package app.listener.breakdown;

import app.JasimaWorkStationListener;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;

public class BreakdownListener extends JasimaWorkStationListener {

	private SummaryStat[] breakdownTimePerMachine;
	private SummaryStat[] repairTimePerMachine;
	private SummaryStat[] upTimePerMachine;

	private SummaryStat breakdownTimeAllMachines;
	private SummaryStat repairTimeAllMachines;
	private SummaryStat upTimeAllMachines;

	private BreakdownStartStat[] brokenDownMachines;

	private boolean[] previouslyDeactivated;
	private boolean[] previouslyRepaired;
	private boolean previouslyDeactivatedAny;
	private boolean previouslyRepairedAny;
	private int numMachines;

	public BreakdownListener() {
	}

	@Override
	protected void init(WorkStation m) {
		numMachines = m.shop().machines.length;

		clear();
	}

	@Override
	protected void activated(WorkStation m, IndividualMachine justActivated) {
		int index = m.index();
		if (!previouslyDeactivated[index]) {
			return;
		}

		if (brokenDownMachines[index].machine != m) {
			throw new RuntimeException("The machine previously recorded to be broken down does not match the current activated machine.");
		}

		BreakdownStartStat startStat = brokenDownMachines[index];
		double breakdownTime = startStat.breakdownTime;
		double repairTime = m.shop().simTime() - breakdownTime;

		repairTimePerMachine[index].value(repairTime);
		repairTimeAllMachines.value(repairTime);

		brokenDownMachines[index] = null;

		previouslyRepaired[index] = true;
		previouslyRepairedAny = true;
	}

	@Override
	protected void deactivated(WorkStation m, IndividualMachine justDeactivated) {
		int index = m.index();

		if (brokenDownMachines[index] != null) {
			throw new RuntimeException("The machine should not currently be broken down.");
		}

		BreakdownStartStat stat = new BreakdownStartStat();
		double breakdownTime = m.shop().simTime();

		stat.machine = m;
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

	public boolean hasBrokenDown(WorkStation machine) {
		return previouslyDeactivated[machine.index()];
	}

	public SummaryStat getMachineBreakdownStat(WorkStation machine) {
		return breakdownTimePerMachine[machine.index()];
	}

	public boolean hasBeenRepaired(WorkStation machine) {
		return previouslyRepaired[machine.index()];
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

	public boolean hasBeenRepairedAnyMachine() {
		return previouslyRepairedAny;
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
		previouslyRepaired = new boolean[numMachines];
		previouslyDeactivatedAny = false;
		previouslyRepairedAny = false;

		for (int i = 0; i < numMachines; i++) {
			breakdownTimePerMachine[i] = new SummaryStat();
			repairTimePerMachine[i] = new SummaryStat();
			upTimePerMachine[i] = new SummaryStat();

			previouslyDeactivated[i] = false;
			previouslyRepaired[i] = false;
		}
	}

	private class BreakdownStartStat {
		WorkStation machine;

		double breakdownTime;
	}

}
