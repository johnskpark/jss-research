package app.listener.breakdown;

import app.IWorkStationListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

// TODO this collects information about the mean repair time, mean time between breakdowns, etc.
// for the problem that can be used by the dispatching rules.
public class BreakdownStat implements IWorkStationListener {



	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
