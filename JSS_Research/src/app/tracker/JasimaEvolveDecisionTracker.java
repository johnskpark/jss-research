package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.IJasimaGPPriorityRule;
import app.listener.IWorkStationListener;
import app.simConfig.AbsSimConfig;
import ec.Individual;

// TODO right, the tracker needs to be able to do the following:
// - Get the decision made by the sequencing rule on which job was selected to be processed.
// - Get the decisions made by the individual components that make up the sequencing rule.
// - Get the priorities assigned to each of the jobs by the individuals components.

// I'm wondering: Is there a better way to implement the ensemble rules for Jasima?
// Could I have something where the EnsemblePriorityRule is aggregated from BasicPriorityRule?

// FIXME IWorkStationListener is part of simConfig, I need to move it elsewhere.
public class JasimaEvolveDecisionTracker implements IWorkStationListener {

	public static final int NOT_SET = -1;

	private IJasimaGPPriorityRule priorityRule;
	private AbsSimConfig simConfig;

	private Map<Integer, List<JasimaEvolveDispatchingDecision>> experimentDecisionMap = new HashMap<Integer, List<JasimaEvolveDispatchingDecision>>();

	private List<JasimaEvolveDispatchingDecision> currentExperimentDecisions = null;
	private int currentExperimentIndex = NOT_SET;
	private JasimaEvolveDispatchingDecision currentDecision = null;

	public JasimaEvolveDecisionTracker() {
		// Empty constructor.
	}

	// Getters

	public IJasimaGPPriorityRule getPriorityRule() {
		return priorityRule;
	}

	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	// Setters

	public void setPriorityRule(IJasimaGPPriorityRule priorityRule) {
		this.priorityRule = priorityRule;
	}

	public void setSimConfig(AbsSimConfig simConfig) {
		this.simConfig = simConfig;
	}

	protected void preloadDispatchingDecision() {

		currentDecision = new JasimaEvolveDispatchingDecision();
	}

	// TODO this is the weirdest shit I've written, I'll need to make this a little more clear in the future.
	public void addExperimentIndex(int index) {
		if (experimentDecisionMap.containsKey(index)) {
			throw new IllegalStateException("The experiment's index must be unique!");
		}

		experimentDecisionMap.put(index, new ArrayList<JasimaEvolveDispatchingDecision>());

		currentExperimentDecisions = experimentDecisionMap.get(index);
		currentExperimentIndex = index;
	}

	public List<JasimaEvolveDispatchingDecision> getCurrentExperimentDecisions() {
		return currentExperimentDecisions;
	}

	public int getCurrentExperimentIndex() {
		return currentExperimentIndex;
	}

	public JasimaEvolveDispatchingDecision getCurrentDecision() {
		return currentDecision;
	}

	// TODO definitely need javadoc that describes the preloading scenario.
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		if (currentDecision != null) {
			if (index == 0) {
				throw new IllegalArgumentException("The caller attempted to add the first individual after the dispatching decision has been initialised.");
			}
			preloadDispatchingDecision();
		} else if (index != 0) {
			throw new IllegalArgumentException("The caller attempted to add individuals before the dispatching decision has been initialised.");
		}

		currentDecision.addPriority(ind, entry, priority);
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// Listen to only the job selected notifications.
		if (event == WorkStation.WS_JOB_SELECTED) {
			if (currentDecision == null) {
				throw new IllegalArgumentException("The dispatching decision has not been initialised yet.");
			}

			// Add in the start time and such information into the decision.
			PrioRuleTarget entry = notifier.justStarted;
			currentDecision.setStartedEntry(entry);
			currentDecision.setStartTime(entry.getShop().simTime());

			// Do some post processing on the current decision.
			currentDecision.addEntryRankings(priorityRule.getEntryRankings());

			// Add the dispatching decision to the list.
			currentExperimentDecisions.add(currentDecision);
			currentDecision = null;
		}
	}

	/**
	 * Get the results of all dispatching decisions made for a problem instance.
	 */
	public Map<Integer, List<JasimaEvolveDispatchingDecision>> getResults() {
		return experimentDecisionMap;
	}

	@Override
	public void clear() {
		priorityRule = null;
		simConfig = null;

		experimentDecisionMap.clear();

		clearCurrentExperiment();
	}

	public void clearCurrentExperiment() {
		currentExperimentDecisions = null;
		currentExperimentIndex = NOT_SET;
		currentDecision = null;
	}

}
