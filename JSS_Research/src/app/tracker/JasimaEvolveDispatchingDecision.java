package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.IJasimaGPPriorityRule;
import app.simConfig.AbsSimConfig;
import ec.Individual;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaEvolveDispatchingDecision {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private PrioRuleTarget startedEntry;

	private Map<Individual, Map<PrioRuleTarget, Double>> decisionMakers = new HashMap<Individual, Map<PrioRuleTarget, Double>>();

	// TODO combine everything together.

	public JasimaEvolveDispatchingDecision() {
		// Keep the constructor empty for now.
	}

	public void postprocessing(IJasimaGPPriorityRule priorityRule, AbsSimConfig simConfig) {
		List<PrioRuleTarget> jobRankings = priorityRule.getJobRankings();

		// TODO
	}

	// Getters

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
