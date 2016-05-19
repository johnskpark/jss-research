package app.tracker;

import java.util.ArrayList;
import java.util.List;

import app.evolution.IJasimaGPPriorityRule;
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
public class JasimaExperimentTracker {

	public static final int NOT_SET = -1;

	private IJasimaGPPriorityRule priorityRule;
	private SimConfig simConfig;

	private List<JasimaExperiment> experimentDecisions;

	private JasimaExperiment currentExperimentDecisions = null;
	private int currentExperimentIndex = NOT_SET;

	public JasimaExperimentTracker() {
		// Empty constructor.
	}

	// Getters

	public IJasimaGPPriorityRule getPriorityRule() {
		return priorityRule;
	}

	public SimConfig getSimConfig() {
		return simConfig;
	}

	// Setters

	public void setPriorityRule(IJasimaGPPriorityRule priorityRule) {
		this.priorityRule = priorityRule;
	}

	public void setSimConfig(SimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public void initialise() {
		experimentDecisions = new ArrayList<JasimaExperiment>(simConfig.getNumConfigs());

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			experimentDecisions.add(new JasimaExperiment(priorityRule.getIndividuals()));
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
	public JasimaExperiment getCurrentExperiment() {
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
	public void addPriority(int index, Object rule, PrioRuleTarget entry, double priority) {
		currentExperimentDecisions.addPriority(index, rule, entry, priority);
	}

	/**
	 * Set the entry that is selected to be processed.
	 */
	public void addSelectedEntry(PrioRuleTarget entry) {
		currentExperimentDecisions.addSelectedEntry(entry);
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
	public void addEntryRankings(List<PrioRuleTarget> entryRankings) {
		currentExperimentDecisions.addEntryRankings(entryRankings);
	}

	/**
	 * Get the results of all dispatching decisions made for a problem instance.
	 */
	public List<JasimaExperiment> getResults() {
		return experimentDecisions;
	}

	public void clear() {
		experimentDecisions.clear();

		clearCurrentExperiment();
	}

	public void clearCurrentExperiment() {
		currentExperimentDecisions = null;
		currentExperimentIndex = NOT_SET;
	}

}
