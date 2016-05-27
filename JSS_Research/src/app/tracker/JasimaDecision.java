package app.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.IMultiRule;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaDecision<T> {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private List<PrioRuleTarget> entryRankings = new ArrayList<PrioRuleTarget>();

	private PrioRuleTarget startedEntry;

	private Map<IMultiRule<T>, SolverData<T>> solvers;
	private Map<IMultiRule<T>, JasimaPriorityStat[]> stats;

	public JasimaDecision(List<PrioRuleTarget> entries, Map<IMultiRule<T>, SolverData<T>> solvers, Map<IMultiRule<T>, JasimaPriorityStat[]> decisions) {
		this.entries = entries;

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

	public List<PrioRuleTarget> getEntryRankings() {
		return entryRankings;
	}

	public PrioRuleTarget getSelectedEntry() {
		return startedEntry;
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

	public void setEntryRankings(List<PrioRuleTarget> entryRankings) {
		this.entryRankings = entryRankings;
	}

	public void setSelectedEntry(PrioRuleTarget startedEntry) {
		this.startedEntry = startedEntry;
	}

}
