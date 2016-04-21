package app.tracker;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

// TODO trying to figure out how to do this efficiently.
// Also, need to record which job that's voted for by the individual.
public class JasimaEvolveExperiment {

	private Individual[] inds;

	private List<JasimaEvolveDecisionMaker> decisionMakers;

	private List<JasimaEvolveDecision> experimentDecisions;

	private JasimaEvolveDecision currentDecision;
	private JasimaPriorityStat[] currentStats;

	/**
	 * Initialise the experiment data with the individuals that make up the rule.
	 */
	public JasimaEvolveExperiment(Individual[] inds) {
		this.inds = inds;

		decisionMakers = new ArrayList<JasimaEvolveDecisionMaker>(inds.length);

		for (int i = 0; i < inds.length; i++) {
			List<JasimaPriorityStat> priorityStats = new ArrayList<JasimaPriorityStat>();

			JasimaEvolveDecisionMaker decisionMaker = new JasimaEvolveDecisionMaker();
			decisionMaker.setPriorityStats(priorityStats);

			decisionMakers.add(decisionMaker);
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

		currentStats = new JasimaPriorityStat[inds.length];

		for (int i = 0; i < inds.length; i++) {
			JasimaPriorityStat stat = new JasimaPriorityStat(q.size());

			currentStats[i] = stat;
			decisionMakers.get(i).addStat(stat);
		}

		currentDecision = new JasimaEvolveDecision(entries, inds, currentStats);
		experimentDecisions.add(currentDecision);
	}

	/**
	 * Add in the priority assigned to an entry by one of the individuals in the experiment.
	 */
	public void addPriority(int index, Individual ind, PrioRuleTarget entry, double priority) {
		currentStats[index].addPriority(entry, priority);
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

	public Individual[] getIndividuals() {
		return inds;
	}

	public List<JasimaEvolveDecisionMaker> getDecisionMakers() {
		return decisionMakers;
	}

	public List<JasimaEvolveDecision> getDecisions() {
		return experimentDecisions;
	}

}
