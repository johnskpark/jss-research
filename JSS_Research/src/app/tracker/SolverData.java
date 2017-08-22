package app.tracker;

import java.util.ArrayList;
import java.util.List;

import app.ITrackedRule;

// TODO come up with a better name in the future.
class SolverData<T> {
	private ITrackedRule<T> solver;

	private List<T> ruleComponents = new ArrayList<>();
	private List<JasimaDecisionMaker> decisionMakers = new ArrayList<>();

	public SolverData(ITrackedRule<T> s) {
		solver = s;

		ruleComponents.addAll(solver.getRuleComponents());

		for (int i = 0; i < ruleComponents.size(); i++) {
			List<JasimaPriorityStat> priorityStats = new ArrayList<>();

			JasimaDecisionMaker decisionMaker = new JasimaDecisionMaker();
			decisionMaker.setPriorityStats(priorityStats);

			decisionMakers.add(decisionMaker);
		}
	}

	public ITrackedRule<T> getSolver() {
		return solver;
	}

	public List<T> getRuleComponents() {
		return ruleComponents;
	}

	public List<JasimaDecisionMaker> getDecisionMakers() {
		return decisionMakers;
	}

	public void addPriorityStat(int index, JasimaPriorityStat stat) {
		decisionMakers.get(index).addStat(stat);
	}

}