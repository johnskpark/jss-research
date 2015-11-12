package app.dispatchingDecision;

import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaDispatchingDecision {

	private double decisionTime;
	private double startTime;

	private PrioRuleTarget[] entries; // TODO how will this be used?
	private JasimaDecisionMaker[] decisionMakers; // TODO how will this be used?

	public JasimaDispatchingDecision() {
		// Keep the constructor empty for now.
	}

	// Getters

	public double getDecisionTime() {
		return decisionTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public PrioRuleTarget[] getEntries() {
		return entries;
	}

	public JasimaDecisionMaker[] getDecisionMakers() {
		return decisionMakers;
	}

	// Setters

	public void setDecisionTime(double decisionTime) {
		this.decisionTime = decisionTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(PrioRuleTarget[] entries) {
		this.entries = entries;
	}

	public void setDecisionMakers(JasimaDecisionMaker[] decisionMakers) {
		this.decisionMakers = decisionMakers;
	}

}
