package app.evolution;

import jasima.shopSim.core.PrioRuleTarget;
import ec.gp.GPData;

public class JasimaGPData extends GPData {

	private static final long serialVersionUID = -7423268350849556385L;

	private PrioRuleTarget entry;

	private double priority;

	public PrioRuleTarget getPrioRuleTarget() {
		return entry;
	}

	public void setPrioRuleTarget(PrioRuleTarget entry) {
		this.entry = entry;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

}
