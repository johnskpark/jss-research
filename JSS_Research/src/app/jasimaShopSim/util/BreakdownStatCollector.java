package app.jasimaShopSim.util;

import java.util.HashMap;
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

	private Map<WorkStation, Double> lastBreakdownTime = new HashMap<WorkStation, Double>();
	private Map<WorkStation, Double> lastRepairTime = new HashMap<WorkStation, Double>();

	public BreakdownStatCollector() {
		super();
	}

	@Override
	protected void init(WorkStation m) {
		stationDisruption = new SummaryStat();
		avgRepairTime = new SummaryStat();
		avgTimeBetweenBreakdowns = new SummaryStat();
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

			double repairTime = shop.simTime() - lastBreakdownTime.get(m);
			avgRepairTime.value(repairTime);

			lastRepairTime.put(m, repairTime);
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
			breakdownTime = shop.simTime() - (lastBreakdownTime.get(m) + lastRepairTime.get(m));
		} else {
			breakdownTime = shop.simTime();
			prevDeactivated.add(m);
		}

		stationDisruption.value(jobProgress);
		avgTimeBetweenBreakdowns.value(breakdownTime);

		lastBreakdownTime.put(m, breakdownTime);
	}

}
