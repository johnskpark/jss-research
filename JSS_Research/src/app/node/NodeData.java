package app.node;

import app.IWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;

public class NodeData {

	private PrioRuleTarget entry;
	private IWorkStationListener listener;

	public NodeData() {
	}

	// Getters

	public PrioRuleTarget getEntry() {
		return entry;
	}

	public IWorkStationListener getWorkStationListener() {
		return listener;
	}

	// Setters

	public void setEntry(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setWorkStationListener(IWorkStationListener listener) {
		this.listener = listener;
	}
}
