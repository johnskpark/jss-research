package app.tracker;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

// FIXME IWorkStationListener is part of simConfig, I need to move it elsewhere.
public class JasimaEvolveDecisionTracker implements IWorkStationListener {

	// So what's the hierarchy going to be?

	private PR tieBreaker;
	private AbsSimConfig simConfig;

	private List<JasimaEvolveDispatchingDecision> allDecisions = new ArrayList<JasimaEvolveDispatchingDecision>();
	private JasimaEvolveDispatchingDecision currentDecision = null;

	public JasimaEvolveDecisionTracker() {
		// FIXME: Probably fill this one out after I finish an implementation.
	}

	public void setTieBreaker(PR tieBreaker) {
		this.tieBreaker = tieBreaker;
	}

	public void setSimConfig(AbsSimConfig simConfig) {
		this.simConfig = simConfig;
	}

	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		if (currentDecision != null) {
			if (index == 0) {
				throw new IllegalArgumentException("TODO: Add in a proper error message sometime later down the line.");
			}

			currentDecision = new JasimaEvolveDispatchingDecision();
		} else if (index != 0) {
			throw new IllegalArgumentException("TODO: Add in a proper error message sometime later down the line.");
		}

		Map<Individual, Map<PrioRuleTarget, Double>> decisionMakers = currentDecision.getDecisionMakers();
		if (!decisionMakers.containsKey(ind)) {
			decisionMakers.put(ind, new HashMap<PrioRuleTarget, Double>());
		}

		if (decisionMakers.get(ind).containsKey(entry)) {
			throw new IllegalArgumentException("TODO: Add in a proper error message sometime later down the line.");
		}

		decisionMakers.get(ind).put(entry, priority);

		List<PrioRuleTarget> entries = currentDecision.getEntries();
		if (!entries.contains(entry)) {
			entries.add(entry);
		}
	}

	@Override
	public void update(WorkStation notifier, WorkStationEvent event) {
		// Listen to only the job selected notifications.
		if (event == WorkStation.WS_JOB_SELECTED) {
			// TODO Add in the start time and such information into the decision.

			// Add the dispatching decision to the list.
			allDecisions.add(currentDecision);
			currentDecision = null;
		}
	}

	public List<JasimaEvolveDispatchingDecision> getResults() {
		return allDecisions;
	}

	public void clear() {
		allDecisions.clear();
		currentDecision = null;
	}

}
