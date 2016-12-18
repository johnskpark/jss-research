package app.tracker;

import java.util.ArrayList;
import java.util.List;

import app.Clearable;
import app.IMultiRule;
import app.simConfig.SimConfig;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

// TODO right, the tracker needs to be able to do the following:
// - Get the decision made by the sequencing rule on which job was selected to be processed.
// - Get the decisions made by the individual components that make up the sequencing rule.
// - Get the priorities assigned to each of the jobs by the individuals components.

// I'm wondering: Is there a better way to implement the ensemble rules for Jasima?
// Could I have something where the EnsemblePriorityRule is aggregated from BasicPriorityRule?

// Is there a way to reduce the memory usage for this?

// TODO Also, I need to change this up to match the decision scenario situation.
public class JasimaExperimentTracker<T> implements Clearable {

	public static final int NOT_SET = -1;

	private List<IMultiRule<T>> priorityRules;
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

	public List<? extends IMultiRule<T>> getPriorityRules() {
		return priorityRules;
	}

	public SimConfig getSimConfig() {
		return simConfig;
	}

	// Setters

	public void setPriorityRule(List<IMultiRule<T>> prs) {
		this.priorityRules = prs;
	}

	public void setSimConfig(SimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public void addRule(IMultiRule<T> rule) {
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
	public void addPriority(IMultiRule<T> solver, int index, T rule, PrioRuleTarget entry, double priority) {
		currentExperimentDecisions.addPriority(solver, index, rule, entry, priority);
	}

	/**
	 * Set the entry that is selected to be processed.
	 */
	public void addSelectedEntry(IMultiRule<T> solver, PrioRuleTarget entry) {
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
	public void addEntryRankings(IMultiRule<T> solver, List<PrioRuleTarget> entryRankings) {
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
