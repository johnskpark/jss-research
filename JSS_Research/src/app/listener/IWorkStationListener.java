package app.listener;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public interface IWorkStationListener extends NotifierListener<WorkStation, WorkStationEvent> {

	/**
	 * Clear the listener in preparation for the next experiment.
	 */
	public abstract void clear();

}
