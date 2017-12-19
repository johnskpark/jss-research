package app;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

public interface IJasimaWorkStationListener extends NotifierListener<WorkStation, WorkStationEvent>, Clearable {

}
