package app.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.ITrackedRule;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class JasimaExperiment<T> {

	private List<ITrackedRule<T>> solvers;
	private List<SolverData<T>> solverData;

	private List<JasimaDecision<T>> experimentDecisions;
	private Map<DecisionEvent, JasimaDecision<T>> experimentDecisionMap;

	private JasimaDecision<T> currentDecision;
	private JasimaPriorityStat[][] currentStats;

	/**
	 * Initialise the experiment data with the solvers and the components that make up the solvers.
	 */
	public JasimaExperiment(List<ITrackedRule<T>> solvers) {
		this.solvers = solvers;
		this.solverData = new ArrayList<>();

		for (ITrackedRule<T> solver : solvers) {
			solverData.add(new SolverData<T>(solver));
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

		currentStats = new JasimaPriorityStat[solvers.size()][];

		for (int i = 0; i < solvers.size(); i++) {
			SolverData<T> data = solverData.get(i);

			JasimaPriorityStat[] stats = new JasimaPriorityStat[data.getRuleComponents().size()];

			for (int j = 0; j < data.getRuleComponents().size(); j++) {
				JasimaPriorityStat stat = new JasimaPriorityStat(q.size());

				data.addPriorityStat(j, stat);
				stats[j] = stat;
			}

			currentStats[i] = stats;
		}

		currentDecision = new JasimaDecision<T>(entries, solvers, solverData, currentStats);
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
	public void addPriority(ITrackedRule<T> solver, int index, T rule, PrioRuleTarget entry, double priority) {
		int solverIndex = solvers.indexOf(solver);
		
		JasimaPriorityStat[] stats = currentStats[solverIndex];

		stats[index].addPriority(entry, priority);
	}

	/**
	 * Set the entry selected to be processed by the rule.
	 */
	public void addSelectedEntry(ITrackedRule<T> solver, PrioRuleTarget entry) {
		currentDecision.setSelectedEntry(solver, entry);
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
	public void addEntryRankings(ITrackedRule<T> solver, List<PrioRuleTarget> rankings) {
		currentDecision.setEntryRankings(solver, rankings);
	}

	// Getters

	public List<T> getRuleComponents(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return solverData.get(index).getRuleComponents();
	}

	public List<JasimaDecisionMaker> getDecisionMakers(ITrackedRule<T> solver) {
		int index = solvers.indexOf(solver);
		
		return solverData.get(index).getDecisionMakers();
	}

	public List<JasimaDecision<T>> getDecisions() {
		return experimentDecisions;
	}

	private DecisionEvent getDecisionEvent(PriorityQueue<?> q) {
		PrioRuleTarget entry = q.get(0);

		return new DecisionEvent(entry.getCurrMachine(), entry.getShop().simTime());
	}

}
