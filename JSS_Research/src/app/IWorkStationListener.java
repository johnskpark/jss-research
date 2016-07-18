package app;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

// FIXME I need to homogenise this with WorkStationListenerBase some point in the future.
public interface IWorkStationListener extends NotifierListener<WorkStation, WorkStationEvent> {

	/**
	 * Clear the listener in preparation for the next experiment.
	 */
	public void clear();

}
