package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.IMultiRule;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaDecision<T> {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	 
	private Map<IMultiRule<T>, List<PrioRuleTarget>> entryRankings;
	private Map<IMultiRule<T>, PrioRuleTarget> startedEntry;

	private Map<IMultiRule<T>, SolverData<T>> solvers;
	private Map<IMultiRule<T>, JasimaPriorityStat[]> stats;

	public JasimaDecision(List<PrioRuleTarget> entries, Map<IMultiRule<T>, SolverData<T>> solvers, Map<IMultiRule<T>, JasimaPriorityStat[]> decisions) {
		this.entries = entries;

		this.entryRankings = new HashMap<>();
		this.startedEntry = new HashMap<>();
		
		this.solvers = solvers;
		this.stats = decisions;
	}

	public void addPriority(IMultiRule<T> solver, int index, PrioRuleTarget entry, double priority) {
		stats.get(solver)[index].addPriority(entry, priority);
	}

	// Getters

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public List<PrioRuleTarget> getEntryRankings(IMultiRule<T> solver) {
		return entryRankings.get(solver);
	}

	public PrioRuleTarget getSelectedEntry(IMultiRule<T> solver) {
		return startedEntry.get(solver);
	}

	public List<T> getRules(IMultiRule<T> solver) {
		return solvers.get(solver).getRuleComponents();
	}

	public JasimaPriorityStat[] getStats(IMultiRule<T> solver) {
		return stats.get(solver);
	}

	// Setters

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setEntryRankings(IMultiRule<T> solver, List<PrioRuleTarget> entryRankings) {
		this.entryRankings.put(solver, entryRankings);
	}

	public void setSelectedEntry(IMultiRule<T> solver, PrioRuleTarget startedEntry) {
		this.startedEntry.put(solver, startedEntry);
	}

}
