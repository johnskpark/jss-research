package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.IMultiRule;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

// TODO trying to figure out how to do this efficiently.
// Also, need to record which job that's voted for by the individual.
public class JasimaExperiment<T> {

	private Map<IMultiRule<T>, SolverData<T>> ruleMap;

	private List<JasimaDecision<T>> experimentDecisions;
	private Map<DecisionEvent, JasimaDecision<T>> experimentDecisionMap;

	private JasimaDecision<T> currentDecision;
	private Map<IMultiRule<T>, JasimaPriorityStat[]> currentStats;

	/**
	 * Initialise the experiment data with the solvers and the components that make up the solvers.
	 */
	public JasimaExperiment(List<IMultiRule<T>> solvers) {
		ruleMap = new HashMap<>();

		for (IMultiRule<T> solver : solvers) {
			ruleMap.put(solver, new SolverData<T>(solver));
		}

		experimentDecisions = new ArrayList<>();
		experimentDecisionMap = new HashMap<>();
	}

	/**
	 * Load up a dispatching decision to the experiment with the specified priority queue.
	 */
	public void addDispatchingDecision(PriorityQueue<?> q) {
		DecisionEvent event = getDecisionEvent(q);
		// Ensure that duplicate dispatching decisions are not added.
		if (experimentDecisionMap.containsKey(event)) {
			return;
		}

		List<PrioRuleTarget> entries = new ArrayList<PrioRuleTarget>(q.size());
		for (int i = 0; i < q.size(); i++) {
			entries.add(q.get(i));
		}

		for (IMultiRule<T> solver : ruleMap.keySet()) {
			SolverData<T> data = ruleMap.get(solver);

			JasimaPriorityStat[] stats = new JasimaPriorityStat[data.getRuleComponents().size()];

			for (int i = 0; i < data.getRuleComponents().size(); i++) {
				stats[i] = new JasimaPriorityStat(q.size());

				// TODO Add this to the solver.
				decisionMakers.get(i).addStat(stats[i]);
			}

			currentStats.put(solver, stats);
		}

		currentDecision = new JasimaDecision<T>(entries, ruleMap, currentStats);
		experimentDecisions.add(currentDecision);
		experimentDecisionMap.put(event, currentDecision);
	}

	public boolean hasDispatchingDecision(PriorityQueue<?> q) {
		DecisionEvent event = getDecisionEvent(q);

		return experimentDecisionMap.containsKey(event);
	}

	/**
	 * Add in the priority assigned to an entry by one of the individuals in the experiment.
	 */
	public void addPriority(IMultiRule<T> solver, int index, T rule, PrioRuleTarget entry, double priority) {
		currentStats.get(solver)[index].addPriority(entry, priority);
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

	public List<T> getRuleComponents(IMultiRule<T> solver) {
		return ruleMap.get(solver).getRuleComponents();
	}

	public List<JasimaDecision<T>> getDecisions() {
		return experimentDecisions;
	}

	private DecisionEvent getDecisionEvent(PriorityQueue<?> q) {
		PrioRuleTarget entry = q.get(0);

		return new DecisionEvent(entry.getCurrMachine(), entry.getShop().simTime());
	}

}
