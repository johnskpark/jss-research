package app.node;

import app.listener.AbsWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;

public class NodeData {

	private PrioRuleTarget entry;
	private AbsWorkStationListener listener;

	public NodeData() {
	}

	public PrioRuleTarget getEntry() {
		return entry;
	}

	public AbsWorkStationListener getWorkStationListener() {
		return listener;
	}

	public void setEntry(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public void setWorkStationListener(AbsWorkStationListener listener) {
		this.listener = listener;
	}
}
