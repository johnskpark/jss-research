package app.jasimaShopSim.util;

import java.util.Map;

import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.IndividualMachine.MachineState;
import jasima.shopSim.util.WorkStationListenerBase;

public class BreakdownStatCollector extends WorkStationListenerBase {

	// Statistics for the machine disruption.
	public SummaryStat stationDisruption;
	public SummaryStat avgRepairTime;
	public SummaryStat avgTimeBetweenBreakdowns;

	private boolean prevDeactivated = false;

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

	// So in the default implementation, the shop floor waits until the operation
	// finishes before the machine gets taken down? What kind of bullshit is this?
	// What do I need to modify before I can get this working?

	//

	@Override
	protected void activated(WorkStation m, IndividualMachine justActivated) {
		if (justActivated.state != MachineState.DOWN && prevDeactivated) {
			// TODO need to find the variable that determines when the machine failed.
			// Also needs to ignore the first time it deactivates.
			double deactivateTime = justActivated.procStarted; // TODO not correct.
			double activateTime = m.shop().simTime();
		}
	}

	@Override
	protected void deactivated(WorkStation m, IndividualMachine justDeactivated) {
		if (justDeactivated.state == MachineState.DOWN) {
			// TODO need to find the

			prevDeactivated = true;
		}
	}

}
