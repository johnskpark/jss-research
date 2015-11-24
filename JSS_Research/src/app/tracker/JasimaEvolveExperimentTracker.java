package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.ArrayList;
import java.util.List;

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
public class JasimaEvolveExperimentTracker implements IWorkStationListener {

	public static final int NOT_SET = -1;

	private IJasimaGPPriorityRule priorityRule;
	private AbsSimConfig simConfig;

	private List<JasimaEvolveExperiment> experimentDecisions;

	private JasimaEvolveExperiment currentExperimentDecisions = null;
	private int currentExperimentIndex = NOT_SET;

	public JasimaEvolveExperimentTracker() {
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

	public void initialise() {
		experimentDecisions = new ArrayList<JasimaEvolveExperiment>(simConfig.getNumConfigs());

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			experimentDecisions.add(new JasimaEvolveExperiment(priorityRule.getIndividuals()));
		}
	}

	/**
	 * TODO javadoc.
	 */
	public void setExperimentIndex(int index) {
		currentExperimentDecisions = experimentDecisions.get(index);
		currentExperimentIndex = index;
	}

	public JasimaEvolveExperiment getCurrentExperiment() {
		return currentExperimentDecisions;
	}

	public int getCurrentExperimentIndex() {
		return currentExperimentIndex;
	}

	/**
	 * TODO javadoc.
	 */
	public void addDispatchingDecision(PriorityQueue<?> q) {
		currentExperimentDecisions.addDispatchingDecision(q);
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		currentExperimentDecisions.addPriority(index, ind, entry, priority);
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// Listen to only the job selected notifications.
		if (event == WorkStation.WS_JOB_SELECTED) {
			// Add in the start time and such information into the decision.
			PrioRuleTarget entry = notifier.justStarted;
			currentExperimentDecisions.addStartedEntry(entry);
			currentExperimentDecisions.addStartTime(entry.getShop().simTime());

			// Do some post processing on the current decision.
			currentExperimentDecisions.addEntryRankings(priorityRule.getEntryRankings());
		}
	}

	/**
	 * Get the results of all dispatching decisions made for a problem instance.
	 */
	public List<JasimaEvolveExperiment> getResults() {
		return experimentDecisions;
	}

	@Override
	public void clear() {
		experimentDecisions.clear();

		clearCurrentExperiment();
	}

	public void clearCurrentExperiment() {
		currentExperimentDecisions = null;
		currentExperimentIndex = NOT_SET;
	}

}
