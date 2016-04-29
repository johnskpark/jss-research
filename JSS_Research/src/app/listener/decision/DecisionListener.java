package app.listener.decision;

import app.IWorkStationListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public class DecisionListener implements IWorkStationListener {

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		if (event == WorkStation.WS_JOB_SELECTED) {
			operationStart(notifier);
		} else if (event == WorkStation.WS_JOB_COMPLETED){
			operationComplete(notifier);
		} else if (event == WorkStation.WS_INIT) {
			init(notifier);
		}
	}

	private void operationStart(WorkStation machine) {
		// TODO
	}

	private void operationComplete(WorkStation machine) {
		// TODO
	}

	private void init(WorkStation machine) {
		// TODO
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
