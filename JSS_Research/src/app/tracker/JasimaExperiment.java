package app.tracker;

import java.util.ArrayList;
import java.util.List;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

// TODO trying to figure out how to do this efficiently.
// Also, need to record which job that's voted for by the individual.
public class JasimaExperiment {

	private Object[] rule;

	private List<JasimaDecisionMaker> decisionMakers;

	private List<JasimaDecision> experimentDecisions;

	private JasimaDecision currentDecision;
	private JasimaPriorityStat[] currentStats;

	/**
	 * Initialise the experiment data with the individuals that make up the rule.
	 */
	public JasimaExperiment(Object[] rule) {
		this.rule = rule;

		decisionMakers = new ArrayList<JasimaDecisionMaker>(rule.length);

		for (int i = 0; i < rule.length; i++) {
			List<JasimaPriorityStat> priorityStats = new ArrayList<JasimaPriorityStat>();

			JasimaDecisionMaker decisionMaker = new JasimaDecisionMaker();
			decisionMaker.setPriorityStats(priorityStats);

			decisionMakers.add(decisionMaker);
		}

		experimentDecisions = new ArrayList<JasimaDecision>();
	}

	/**
	 * Load up a dispatching decision to the experiment with the specified priority queue.
	 */
	public void addDispatchingDecision(PriorityQueue<?> q) {
		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>(q.size());
		for (int i = 0; i < q.size(); i++) {
			entries.add(q.get(i));
		}

		currentStats = new JasimaPriorityStat[rule.length];

		for (int i = 0; i < rule.length; i++) {
			JasimaPriorityStat stat = new JasimaPriorityStat(q.size());

			currentStats[i] = stat;
			decisionMakers.get(i).addStat(stat);
		}

		currentDecision = new JasimaDecision(entries, rule, currentStats);
		experimentDecisions.add(currentDecision);
	}

	/**
	 * Add in the priority assigned to an entry by one of the individuals in the experiment.
	 */
	public void addPriority(int index, Object rule, PrioRuleTarget entry, double priority) {
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

	public Object[] getRules() {
		return rule;
	}

	public List<JasimaDecisionMaker> getDecisionMakers() {
		return decisionMakers;
	}

	public List<JasimaDecision> getDecisions() {
		return experimentDecisions;
	}

}
