package app.node;

import app.listener.IWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;

public class NodeData {

	private PrioRuleTarget entry;
	private IWorkStationListener listener;

	public NodeData() {
	}

	public PrioRuleTarget getEntry() {
		return entry;
	}

	public IWorkStationListener getWorkStationListener() {
		return listener;
	}

	public void setEntry(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setWorkStationListener(IWorkStationListener listener) {
		this.listener = listener;
	}
}
