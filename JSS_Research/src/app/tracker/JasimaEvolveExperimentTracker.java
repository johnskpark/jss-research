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
public class JasimaEvolveExperimentTracker implements IWorkStationListener {

	public static final int NOT_SET = -1;

	private IJasimaGPPriorityRule priorityRule;
	private AbsSimConfig simConfig;

	private List<JasimaEvolveExperiment> experimentDecisions;

	private JasimaEvolveExperiment currentExperimentDecisions = null;
	private int currentExperimentIndex = NOT_SET;
	private JasimaEvolveDecision currentDecision = null;

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

	public JasimaEvolveExperiment getCurrentExperimentDecisions() {
		return currentExperimentDecisions;
	}

	public int getCurrentExperimentIndex() {
		return currentExperimentIndex;
	}

	public JasimaEvolveDecision getCurrentDecision() {
		return currentDecision;
	}

	/**
	 * TODO javadoc.
	 */
	public void addDispatchingDecision() {
		if (currentDecision != null) {
			throw new IllegalArgumentException("The dispatching decision cannot be initialised more than once.");
		}
		
		// TODO 
		currentExperimentDecisions.addDispatchingDecision();
		currentDecision = new JasimaEvolveDecision(priorityRule.getIndividuals());
	}

	/**
	 * TODO javadoc.
	 */
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		if (currentDecision == null) {
			throw new IllegalArgumentException("The caller attempted to add individuals before the dispatching decision has been initialised.");
		}

		// TODO right, this part needs to be fixed. I think that constructing a hash map every decision is a little too excessive. 
		currentExperimentDecisions.addPriority(index, ind, entry, priority);
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
//			currentExperimentDecisions.add(currentDecision);
			currentDecision = null;
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
		priorityRule = null;
		simConfig = null;

		experimentDecisions.clear();

		clearCurrentExperiment();
	}

	public void clearCurrentExperiment() {
		currentExperimentDecisions = null;
		currentExperimentIndex = NOT_SET;
		currentDecision = null;
	}

}
