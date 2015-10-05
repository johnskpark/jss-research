package app.evolution;

import app.listener.AbsWorkStationListener;
import jasima.shopSim.core.PrioRuleTarget;
import ec.gp.GPData;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;
	private double priority;
	private AbsWorkStationListener workStationListener;

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public void setPrioRuleTarget(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public double getPriority() {
		return priority;
	}

	public AbsWorkStationListener getWorkStationListener() {
		return workStationListener;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setWorkStationListener(AbsWorkStationListener listener) {
		this.workStationListener = listener;
	}

}
