package app.tracker;

import java.util.ArrayList;
import java.util.List;

import app.Clearable;
import app.ITrackedRule;
import app.simConfig.SimConfig;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class JasimaExperimentTracker<T> implements Clearable {

	public static final int NOT_SET = -1;

	private List<ITrackedRule<T>> priorityRules;
	private SimConfig simConfig;

	private List<JasimaExperiment<T>> experimentDecisions;

	private JasimaExperiment<T> currentExperimentDecisions = null;
	private int currentExperimentIndex = NOT_SET;

	public JasimaExperimentTracker() {
		// Empty constructor.

		priorityRules = new ArrayList<>();
		simConfig = null;

		experimentDecisions = new ArrayList<>();
	}

	// Getters

	public List<? extends ITrackedRule<T>> getPriorityRules() {
		return priorityRules;
	}

	public SimConfig getSimConfig() {
		return simConfig;
	}

	// Setters

	public void setPriorityRule(List<ITrackedRule<T>> prs) {
		this.priorityRules = prs;
	}

	public void setSimConfig(SimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public void addRule(ITrackedRule<T> rule) {
		this.priorityRules.add(rule);
	}

	public void initialise() {
		experimentDecisions = new ArrayList<>(simConfig.getNumConfigs());

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			experimentDecisions.add(new JasimaExperiment<>(priorityRules));
		}
	}

	/**
	 * Set the current experiment index.
	 */
	public void setExperimentIndex(int index) {
		currentExperimentDecisions = experimentDecisions.get(index);
		currentExperimentIndex = index;
	}

	/**
	 * Get the current experiment.
	 */
	public JasimaExperiment<T> getCurrentExperiment() {
		return currentExperimentDecisions;
	}

	/**
	 * Get the current experiment's index.
	 */
	public int getCurrentExperimentIndex() {
		return currentExperimentIndex;
	}

	/**
	 * Add the dispatching decision to the current experiment.
	 */
	public void addDispatchingDecision(PriorityQueue<?> q) {
		currentExperimentDecisions.addDispatchingDecision(q);
	}

	/**
	 * Add the priority assigned to an entry for the dispatching decision.
	 */
	public void addPriority(ITrackedRule<T> solver, int index, T rule, PrioRuleTarget entry, double priority) {
		currentExperimentDecisions.addPriority(solver, index, rule, entry, priority);
	}

	/**
	 * Set the entry that is selected to be processed.
	 */
	public void addSelectedEntry(ITrackedRule<T> solver, PrioRuleTarget entry) {
		currentExperimentDecisions.addSelectedEntry(solver, entry);
	}

	/**
	 * Set the start time of the entry that is selected to be processed.
	 */
	public void addStartTime(double startTime) {
		currentExperimentDecisions.addStartTime(startTime);
	}

	/**
	 * Set the rankings set by the rule for the entries in the dispatching decision.
	 */
	public void addEntryRankings(ITrackedRule<T> solver, List<PrioRuleTarget> entryRankings) {
		currentExperimentDecisions.addEntryRankings(solver, entryRankings);
	}

	/**
	 * Get the results of all dispatching decisions made for a problem instance.
	 */
	public List<JasimaExperiment<T>> getResults() {
		return experimentDecisions;
	}

	public void clear() {
		priorityRules.clear();
		experimentDecisions.clear();

		clearCurrentExperiment();
	}

	public void clearCurrentExperiment() {
		currentExperimentDecisions = null;
		currentExperimentIndex = NOT_SET;
	}

}
