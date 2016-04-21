package app.tracker;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaEvolveDecision {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private List<PrioRuleTarget> entryRankings = new ArrayList<PrioRuleTarget>();

	private PrioRuleTarget startedEntry;

	private Individual[] inds;
	private JasimaPriorityStat[] stats;

	public JasimaEvolveDecision(List<PrioRuleTarget> entries, Individual[] inds, JasimaPriorityStat[] decisions) {
		this.entries = entries;

		this.inds = inds;
		this.stats = decisions;
	}

	public void addPriority(int index, PrioRuleTarget entry, double priority) {
		stats[index].addPriority(entry, priority);
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

	public PrioRuleTarget getSelectedEntry() {
		return startedEntry;
	}

	public Individual[] getIndividuals() {
		return inds;
	}

	public JasimaPriorityStat[] getStats() {
		return stats;
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

	public void setIndividuals(Individual[] inds) {
		this.inds = inds;
	}

	public void setDecisions(JasimaPriorityStat[] decisions) {
		this.stats = decisions;
	}

}
