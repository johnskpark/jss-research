package app.node;

import java.util.Map;

import app.IWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;

public class NodeData {

	private PrioRuleTarget entry;
	private Map<String, IWorkStationListener> listener;

	public NodeData() {
	}

	// Getters

	public PrioRuleTarget getEntry() {
		return entry;
	}

	public Map<String, IWorkStationListener> getWorkStationListener() {
		return listener;
	}

	// Setters

	public void setEntry(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setWorkStationListeners(Map<String, IWorkStationListener> listener) {
		this.listener = listener;
	}
}
