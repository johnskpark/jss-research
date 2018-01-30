package app.tracker;

import java.util.ArrayList;
import java.util.List;

import app.ITrackedRule;
import jasima.shopSim.core.PrioRuleTarget;

// So the tracker's going to generate one of these for each dispatching decision.
public class JasimaDecision<T> {

	private double startTime;

	private List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>();
	private List<ITrackedRule<T>> solvers;
	
	private List<SolverData<T>> solverData;
	private JasimaPriorityStat[][] stats;
	
	private List<PrioRuleTarget>[] entryRankings;
	private PrioRuleTarget[] startedEntry;
	
	@SuppressWarnings("unchecked")
	public JasimaDecision(List<PrioRuleTarget> entries,
			List<ITrackedRule<T>> solvers,
			List<SolverData<T>> solverData,
			JasimaPriorityStat[][] decisions) {
		this.entries = entries;
		
		this.solvers = solvers;
		this.solverData = solverData;
		this.stats = decisions;
		
		this.entryRankings = new List[solvers.size()];
		this.startedEntry = new PrioRuleTarget[solvers.size()];
	}

	public void addPriority(ITrackedRule<T> solver, int index, PrioRuleTarget entry, double priority) {
		int ruleIndex = solvers.indexOf(solver);
		
		stats[ruleIndex][index].addPriority(entry, priority);
	}

	// Getters

	public double getStartTime() {
		return startTime;
	}

	public List<PrioRuleTarget> getEntries() {
		return entries;
	}

	public List<PrioRuleTarget> getEntryRankings(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return entryRankings[index];
	}

	public PrioRuleTarget getSelectedEntry(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return startedEntry[index];
	}

	public List<T> getRules(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return solverData.get(index).getRuleComponents();
	}

	public JasimaPriorityStat[] getStats(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return stats[index];
	}

	// Setters

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setEntries(List<PrioRuleTarget> entries) {
		this.entries = entries;
	}

	public void setEntryRankings(ITrackedRule<T> solver, List<PrioRuleTarget> entryRankings) {
		int index = solvers.indexOf(solver);
		
		this.entryRankings[index] = entryRankings;
	}

	public void setSelectedEntry(ITrackedRule<T> solver, PrioRuleTarget startedEntry) {
		int index = solvers.indexOf(solver);
		
		this.startedEntry[index] = startedEntry;	
	}

}
