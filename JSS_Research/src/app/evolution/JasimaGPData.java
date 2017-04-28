package app.evolution;

import java.util.List;

import app.JasimaWorkStationListener;
import ec.gp.GPData;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;
	private double priority;
	private List<JasimaWorkStationListener> workStationListeners;

	// Getters

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public double getPriority() {
		return priority;
	}

	public List<JasimaWorkStationListener> getWorkStationListeners() {
		return workStationListeners;
	}

	public JasimaWorkStationListener getWorkStationListener(String className) {
		for (JasimaWorkStationListener listener : workStationListeners) {
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

	public void setWorkStationListener(List<JasimaWorkStationListener> listener) {
		this.workStationListeners = listener;
	}

}
