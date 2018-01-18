package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.ITrackedRule;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaDecision<T> {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();

	// TODO I should probably replace these with the rules themselves, Maps are horribly inefficient for small sizes.
	private Map<ITrackedRule<T>, List<PrioRuleTarget>> entryRankings;
	private Map<ITrackedRule<T>, PrioRuleTarget> startedEntry;

	private Map<ITrackedRule<T>, SolverData<T>> solvers;
	private Map<ITrackedRule<T>, JasimaPriorityStat[]> stats;

	public JasimaDecision(List<PrioRuleTarget> entries,
			Map<ITrackedRule<T>, SolverData<T>> solvers,
			Map<ITrackedRule<T>, JasimaPriorityStat[]> decisions) {
		this.entries = entries;

		this.entryRankings = new HashMap<>();
		this.startedEntry = new HashMap<>();

		this.solvers = solvers;
		this.stats = decisions;
	}

	public void addPriority(ITrackedRule<T> solver, int index, PrioRuleTarget entry, double priority) {
		stats.get(solver)[index].addPriority(entry, priority);
	}

	// Getters

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public List<PrioRuleTarget> getEntryRankings(ITrackedRule<T> solver) {
		return entryRankings.get(solver);
	}

	public PrioRuleTarget getSelectedEntry(ITrackedRule<T> solver) {
		return startedEntry.get(solver);
	}

	public List<T> getRules(ITrackedRule<T> solver) {
		return solvers.get(solver).getRuleComponents();
	}

	public JasimaPriorityStat[] getStats(ITrackedRule<T> solver) {
		return stats.get(solver);
	}

	// Setters

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setEntryRankings(ITrackedRule<T> solver, List<PrioRuleTarget> entryRankings) {
		this.entryRankings.put(solver, entryRankings);
	}

	public void setSelectedEntry(ITrackedRule<T> solver, PrioRuleTarget startedEntry) {
		this.startedEntry.put(solver, startedEntry);
	}

}
