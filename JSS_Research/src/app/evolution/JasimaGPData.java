package app.evolution;

import java.util.List;

import app.IJasimaWorkStationListener;
import ec.gp.GPData;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;
	private double priority;
	private List<IJasimaWorkStationListener> workStationListeners;

	// Getters

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public double getPriority() {
		return priority;
	}

	public List<IJasimaWorkStationListener> getWorkStationListeners() {
		return workStationListeners;
	}

	public IJasimaWorkStationListener getWorkStationListener(String className) {
		for (IJasimaWorkStationListener listener : workStationListeners) {
			if (listener.getClass().getSimpleName().equals(className)) {
				return listener;
			}
		}

		return null;
	}

	// Setters

	public void setPrioRuleTarget(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setWorkStationListener(List<IJasimaWorkStationListener> listener) {
		this.workStationListeners = listener;
	}

}
