package app.node;

import java.util.Map;

import app.JasimaWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;

public class NodeData {

	private PrioRuleTarget entry;
	private Map<String, JasimaWorkStationListener> listener;

	public NodeData() {
	}

	// Getters

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public Map<String, JasimaWorkStationListener> getWorkStationListeners() {
		return listener;
	}

	// Setters

	public void setEntry(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setWorkStationListeners(Map<String, JasimaWorkStationListener> listener) {
		this.listener = listener;
	}
}
