package app.listener;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;
import ec.Setup;

public abstract class AbsWorkStationListener implements NotifierListener<WorkStation, WorkStationEvent>, Setup {

	private static final long serialVersionUID = -5457997129960940526L;

	/**
	 * Clear the listener in preparation for the next experiment.
	 */
	public abstract void clear();

}
