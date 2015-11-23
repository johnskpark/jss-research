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

	private Map<Individual, JasimaPriorityStat> decisionMakers = new HashMap<Individual, JasimaPriorityStat>();

	public JasimaEvolveDecision(GPIndividual[] inds) {
		for (GPIndividual ind : inds) {
			decisionMakers.put(ind, new JasimaPriorityStat());
		}
	}

	public void addPriority(Individual ind, PrioRuleTarget entry, double priority) {
		decisionMakers.get(ind).add(entry, priority);
	}

	public void addEntryRankings(List<PrioRuleTarget> entryRankings) {
		this.entryRankings = entryRankings;
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

	public Map<Individual, JasimaPriorityStat> getDecisionMakers() {
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

	public void setDecisionMakers(Map<Individual, JasimaPriorityStat> decisionMakers) {
		this.decisionMakers = decisionMakers;
	}

}
