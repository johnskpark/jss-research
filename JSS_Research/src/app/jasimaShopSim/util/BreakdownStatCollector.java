package app.jasimaShopSim.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.jasimaShopSim.core.IndividualBreakdownMachine;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.IndividualMachine.MachineState;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.util.WorkStationListenerBase;

public class BreakdownStatCollector extends WorkStationListenerBase {

	// Statistics for the machine disruption.
	public SummaryStat stationDisruption;
	public SummaryStat avgRepairTime;
	public SummaryStat avgTimeBetweenBreakdowns;

	private Set<WorkStation> prevDeactivated = new HashSet<WorkStation>();

	private double[] lastBreakdownTime;
	private double[] lastRepairTime;

	public BreakdownStatCollector() {
		super();
	}

	@Override
	protected void init(WorkStation m) {
		stationDisruption = new SummaryStat();
		avgRepairTime = new SummaryStat();
		avgTimeBetweenBreakdowns = new SummaryStat();

		WorkStation[] machines = m.shop().machines;
		lastBreakdownTime = new double[machines.length];
		lastRepairTime = new double[machines.length];
	}

	@Override
	protected void produceResults(WorkStation m, Map<String, Object> res) {
		res.put(m.getName() + ".qDisrupt", stationDisruption);
		res.put(m.getName() + ".repair", avgRepairTime);
		res.put(m.getName() + ".breakdownTime", avgTimeBetweenBreakdowns);
	}

	@Override
	protected void activated(WorkStation m, IndividualMachine justActivated) {
		assert justActivated.state != MachineState.DOWN;

		if (prevDeactivated.contains(m)) {
			final JobShop shop = justActivated.workStation.shop();

			double repairTime = shop.simTime() - lastBreakdownTime[m.index()];

			avgRepairTime.value(repairTime);

			lastRepairTime[m.index()] = repairTime;
		}
	}

	@Override
	protected void deactivated(WorkStation m, IndividualMachine justDeactivated) {
		assert justDeactivated.state == MachineState.DOWN;

		final IndividualBreakdownMachine breakdownMachine = (IndividualBreakdownMachine) justDeactivated;
		final JobShop shop = justDeactivated.workStation.shop();

		// Update the station disruption and the average time between breakdowns.
		double jobProgress = 0.0;
		if (justDeactivated.curJob != null) {
			jobProgress = breakdownMachine.procProgress;
		}

		double breakdownTime;
		if (prevDeactivated.contains(m)) {
			breakdownTime = shop.simTime() - (lastBreakdownTime[m.index()] + lastRepairTime[m.index()]);
		} else {
			breakdownTime = shop.simTime();
			prevDeactivated.add(m);
		}

		stationDisruption.value(jobProgress);
		avgTimeBetweenBreakdowns.value(breakdownTime);

		lastBreakdownTime[m.index()] = shop.simTime();
	}

	public double getLastBreakdownTime(WorkStation machine) {
		return lastBreakdownTime[machine.index()];
	}

	public double getLastRepairTime(WorkStation machine) {
		return lastRepairTime[machine.index()];
	}

}
