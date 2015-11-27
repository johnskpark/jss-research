package app.tracker;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;
import ec.gp.GPIndividual;

// TODO trying to figure out how to do this efficiently.
// Also, need to record which job that's voted for by the individual.
public class JasimaEvolveExperiment {

	private GPIndividual[] inds;

	private List<JasimaEvolveDecisionMaker> decisionMakers;
	private Map<GPIndividual, JasimaEvolveDecisionMaker> decisionMakerMap;

	private List<JasimaEvolveDecision> experimentDecisions;

	private JasimaEvolveDecision currentDecision;
	private Map<GPIndividual, JasimaPriorityStat> currentDecisionsMap;

	/**
	 * Initialise the experiment data with the individuals that make up the rule.
	 */
	public JasimaEvolveExperiment(GPIndividual[] inds) {
		this.inds = inds;

		decisionMakers = new ArrayList<JasimaEvolveDecisionMaker>(inds.length);
		decisionMakerMap = new HashMap<GPIndividual, JasimaEvolveDecisionMaker>(inds.length);

		for (GPIndividual ind : inds) {
			List<JasimaPriorityStat> priorityStats = new ArrayList<JasimaPriorityStat>();

			JasimaEvolveDecisionMaker decisionMaker = new JasimaEvolveDecisionMaker();
			decisionMaker.setPriorityStats(priorityStats);

			decisionMakers.add(decisionMaker);
			decisionMakerMap.put(ind, decisionMaker);
		}

		experimentDecisions = new ArrayList<JasimaEvolveDecision>();
	}

	/**
	 * Load up a dispatching decision to the experiment with the specified priority queue.
	 */
	public void addDispatchingDecision(PriorityQueue<?> q) {
		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>(q.size());
		for (int i = 0; i < q.size(); i++) {
			entries.add(q.get(i));
		}

		currentDecisionsMap = new HashMap<GPIndividual, JasimaPriorityStat>();

		for (GPIndividual ind : inds) {
			JasimaPriorityStat stat = new JasimaPriorityStat(q.size());

			currentDecisionsMap.put(ind, stat);
			decisionMakerMap.get(ind).addStat(stat);
		}

		currentDecision = new JasimaEvolveDecision(entries, currentDecisionsMap);
		experimentDecisions.add(currentDecision);
	}

	/**
	 * Add in the priority assigned to an entry by one of the individuals in the experiment.
	 */
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		currentDecisionsMap.get(ind).addPriority(entry, priority);
	}

	/**
	 * Set the entry selected to be processed by the rule.
	 */
	public void addSelectedEntry(PrioRuleTarget entry) {
		currentDecision.setSelectedEntry(entry);
	}

	/**
	 * Set the start time of the entry selected to be processed.
	 */
	public void addStartTime(double time) {
		currentDecision.setStartTime(time);
	}

	/**
	 * Set the ranking for each entry given by the rule.
	 */
	public void addEntryRankings(List<PrioRuleTarget> rankings) {
		currentDecision.setEntryRankings(rankings);
	}

	// Getters

	public Map<GPIndividual, JasimaEvolveDecisionMaker> getDecisionMakers() {
		return decisionMakerMap;
	}

	public List<JasimaEvolveDecision> getDecisions() {
		return experimentDecisions;
	}

}
