package app.evolution;

import java.util.Map;

import app.IWorkStationListener;
import ec.gp.GPData;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;
	private double priority;
	private Map<String, IWorkStationListener> workStationListeners;

	// Getters

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public double getPriority() {
		return priority;
	}

	public Map<String, IWorkStationListener> getWorkStationListeners() {
		return workStationListeners;
	}

	// Setters

	public void setPrioRuleTarget(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setWorkStationListener(Map<String, IWorkStationListener> listener) {
		this.workStationListeners = listener;
	}

}
