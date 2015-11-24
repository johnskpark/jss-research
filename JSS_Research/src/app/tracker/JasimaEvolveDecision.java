package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaEvolveDecision {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private List<PrioRuleTarget> entryRankings = new ArrayList<PrioRuleTarget>();

	private PrioRuleTarget startedEntry;

	private Map<GPIndividual, JasimaPriorityStat> decisionMakers = new HashMap<GPIndividual, JasimaPriorityStat>();

	public JasimaEvolveDecision(List<PrioRuleTarget> entries, Map<GPIndividual, JasimaPriorityStat> decisionMakers) {
		this.entries = entries;
		this.decisionMakers = decisionMakers;
	}

	public void addPriority(Individual ind, PrioRuleTarget entry, double priority) {
		decisionMakers.get(ind).addPriority(entry, priority);
	}

	// Getters

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public List<PrioRuleTarget> getEntryRankings() {
		return entryRankings;
	}

	public PrioRuleTarget getStartedEntry() {
		return startedEntry;
	}

	public Map<GPIndividual, JasimaPriorityStat> getDecisionMakers() {
		return decisionMakers;
	}

	// Setters

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setEntryRankings(List<PrioRuleTarget> entryRankings) {
		this.entryRankings = entryRankings;
	}

	public void setSelectedEntry(PrioRuleTarget startedEntry) {
		this.startedEntry = startedEntry;
	}

	public void setDecisionMakers(Map<GPIndividual, JasimaPriorityStat> decisionMakers) {
		this.decisionMakers = decisionMakers;
	}

}
