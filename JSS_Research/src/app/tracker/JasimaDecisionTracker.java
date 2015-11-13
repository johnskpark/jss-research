package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.ArrayList;
import java.util.List;

import app.listener.IWorkStationListener;
import app.simConfig.AbsSimConfig;
import ec.Individual;
import ec.util.Pair;

// TODO right, the tracker needs to be able to do the following:
// - Get the decision made by the sequencing rule on which job was selected to be processed.
// - Get the decisions made by the individual components that make up the sequencing rule.
// - Get the priorities assigned to each of the jobs by the individuals components.

// I'm wondering: Is there a better way to implement the ensemble rules for Jasima?
// Could I have something where the EnsemblePriorityRule is aggregated from BasicPriorityRule?

public class JasimaDecisionTracker implements IWorkStationListener {

	// So what's the hierarchy going to be?

	private AbsSimConfig simConfig;

	private List<JasimaDispatchingDecision> dispatchingDecisions = new ArrayList<JasimaDispatchingDecision>();

	public JasimaDecisionTracker() {
		// TODO add something to the constructor here.
	}

	public void setSimConfig(AbsSimConfig simConfig) {
		this.simConfig = simConfig;
	}

	// TODO Fuck it, just make the damn thing call this when calcPrio is being called.
	public void addPriority(JasimaPriorityStat decisionMaker, PrioRuleTarget entry, double priority) {
		// TODO
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// Listen to only the job selected notifications.
		if (event == WorkStation.WS_JOB_SELECTED) {
			// TODO Decision was made here.
		}
	}

	public Pair<Individual, List<JasimaDispatchingDecision>> getResults() {
		// TODO

		return null;
	}

	public void clear() {
		// TODO
	}

}
