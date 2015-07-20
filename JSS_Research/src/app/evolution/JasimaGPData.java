package app.evolution;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;
import ec.gp.GPData;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;
	private double priority;
	private NotifierListener<WorkStation, WorkStationEvent> workStationListener;

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public void setPrioRuleTarget(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public double getPriority() {
		return priority;
	}

	public NotifierListener<WorkStation, WorkStationEvent> getWorkStationListener() {
		return workStationListener;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setWorkStationListener(NotifierListener<WorkStation, WorkStationEvent> listener) {
		this.workStationListener = listener;
	}

}
