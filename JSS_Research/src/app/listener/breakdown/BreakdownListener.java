package app.listener.breakdown;

import app.JasimaWorkStationListener;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;

public class BreakdownListener extends JasimaWorkStationListener {

	private ReferenceStat stat = new ReferenceStat();

	public BreakdownListener() {
		super();
	}

	@Override
	protected void init(WorkStation m) {
		stat.numMachines = m.shop().machines.length;

		clear();
	}

	@Override
	protected void activated(WorkStation m, IndividualMachine justActivated) {
		int index = m.index();
		if (!stat.previouslyDeactivated[index]) {
			return;
		}

		if (stat.brokenDownMachines[index].machine != m) {
			throw new RuntimeException("The machine previously recorded to be broken down does not match the current activated machine.");
		}

		BreakdownStartStat startStat = stat.brokenDownMachines[index];
		double breakdownTime = startStat.breakdownTime;
		double repairTime = m.shop().simTime() - breakdownTime;

		stat.repairTimePerMachine[index].value(repairTime);
		stat.repairTimeAllMachines.value(repairTime);

		stat.brokenDownMachines[index] = null;

		stat.previouslyRepaired[index] = true;
		stat.previouslyRepairedAny = true;
	}

	@Override
	protected void deactivated(WorkStation m, IndividualMachine justDeactivated) {
		int index = m.index();

		if (stat.brokenDownMachines[index] != null) {
			throw new RuntimeException("The machine should not currently be broken down.");
		}

		BreakdownStartStat stat = new BreakdownStartStat();
		double breakdownTime = m.shop().simTime();

		stat.machine = m;
		stat.breakdownTime = breakdownTime;

		this.stat.brokenDownMachines[index] = stat;

		if (this.stat.previouslyDeactivated[index]) {
			double upTime = breakdownTime - this.stat.breakdownTimePerMachine[index].lastValue() - this.stat.repairTimePerMachine[index].lastValue();
			this.stat.upTimePerMachine[index].value(upTime);
			this.stat.upTimeAllMachines.value(upTime);
		} else {
			this.stat.upTimePerMachine[index].value(breakdownTime);
			this.stat.upTimeAllMachines.value(breakdownTime);

			this.stat.previouslyDeactivated[index] = true;
		}

		this.stat.breakdownTimePerMachine[index].value(stat.breakdownTime);
		this.stat.breakdownTimeAllMachines.value(stat.breakdownTime);

		this.stat.previouslyDeactivatedAny = true;
	}

	public boolean hasBrokenDown(WorkStation machine) {
		return stat.previouslyDeactivated[machine.index()];
	}

	public SummaryStat getMachineBreakdownStat(WorkStation machine) {
		return stat.breakdownTimePerMachine[machine.index()];
	}

	public boolean hasBeenRepaired(WorkStation machine) {
		return stat.previouslyRepaired[machine.index()];
	}

	public SummaryStat getMachineRepairTimeStat(WorkStation machine) {
		return stat.repairTimePerMachine[machine.index()];
	}

	public SummaryStat getMachineUpTimeStat(WorkStation machine) {
		return stat.upTimePerMachine[machine.index()];
	}

	public boolean hasBrokenDownAnyMachine() {
		return stat.previouslyDeactivatedAny;
	}

	public SummaryStat getAllMachineBreakdownStat() {
		return stat.breakdownTimeAllMachines;
	}

	public boolean hasBeenRepairedAnyMachine() {
		return stat.previouslyRepairedAny;
	}

	public SummaryStat getAllMachineRepairStat() {
		return stat.repairTimeAllMachines;
	}

	public SummaryStat getAllMachineUpTimeStat() {
		return stat.upTimeAllMachines;
	}

	@Override
	public void clear() {
		stat.breakdownTimePerMachine = new SummaryStat[stat.numMachines];
		stat.repairTimePerMachine = new SummaryStat[stat.numMachines];
		stat.upTimePerMachine = new SummaryStat[stat.numMachines];

		stat.breakdownTimeAllMachines = new SummaryStat();
		stat.repairTimeAllMachines = new SummaryStat();
		stat.upTimeAllMachines = new SummaryStat();

		stat.brokenDownMachines = new BreakdownStartStat[stat.numMachines];

		stat.previouslyDeactivated = new boolean[stat.numMachines];
		stat.previouslyRepaired = new boolean[stat.numMachines];
		stat.previouslyDeactivatedAny = false;
		stat.previouslyRepairedAny = false;

		for (int i = 0; i < stat.numMachines; i++) {
			stat.breakdownTimePerMachine[i] = new SummaryStat();
			stat.repairTimePerMachine[i] = new SummaryStat();
			stat.upTimePerMachine[i] = new SummaryStat();

			stat.previouslyDeactivated[i] = false;
			stat.previouslyRepaired[i] = false;
		}
	}

	private class BreakdownStartStat {
		WorkStation machine;

		double breakdownTime;
	}

	// Used explicitly for cloning, since it will be used by the experiment as well.
	class ReferenceStat {
		SummaryStat[] breakdownTimePerMachine;
		SummaryStat[] repairTimePerMachine;
		SummaryStat[] upTimePerMachine;

		SummaryStat breakdownTimeAllMachines;
		SummaryStat repairTimeAllMachines;
		SummaryStat upTimeAllMachines;

		BreakdownStartStat[] brokenDownMachines;

		boolean[] previouslyDeactivated;
		boolean[] previouslyRepaired;
		boolean previouslyDeactivatedAny;
		boolean previouslyRepairedAny;
		int numMachines;
	}

}
