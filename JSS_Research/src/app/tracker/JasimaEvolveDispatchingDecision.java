package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaEvolveDispatchingDecision {

	private double decisionTime;
	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private PrioRuleTarget startedEntry;

	private Map<Individual, Map<PrioRuleTarget, Double>> decisionMakers = new HashMap<Individual, Map<PrioRuleTarget, Double>>();

	// TODO combine everything together.

	public JasimaEvolveDispatchingDecision() {
		// Keep the constructor empty for now.
	}

	public void dispatchingDecisionPostprocessing() {
		// TODO
	}

	// Getters

	public double getDecisionTime() {
		return decisionTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public PrioRuleTarget getStartedEntry() {
		return startedEntry;
	}

	public Map<Individual, Map<PrioRuleTarget, Double>> getDecisionMakers() {
		return decisionMakers;
	}

	// Setters

	public void setDecisionTime(double decisionTime) {
		this.decisionTime = decisionTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setStartedEntry(PrioRuleTarget startedEntry) {
		this.startedEntry = startedEntry;
	}

	public void setDecisionMakers(Map<Individual, Map<PrioRuleTarget, Double>> decisionMakers) {
		this.decisionMakers = decisionMakers;
	}

}
