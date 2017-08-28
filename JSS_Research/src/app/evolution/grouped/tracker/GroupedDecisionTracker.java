package app.evolution.grouped.tracker;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPProblem;
import app.simConfig.DynamicSimConfig;
import app.util.BasicStatistics;
import ec.gp.GPIndividual;
import jasima.core.util.Pair;

public class GroupedDecisionTracker implements IJasimaTracker {

	private static final int SAMPLE_SIZE = 2000;

	private Map<GPIndividual, Pair<BasicStatistics, Integer>> decisionStats = new HashMap<GPIndividual, Pair<BasicStatistics, Integer>>();

	private JasimaGPProblem problem;

	public int getNumIgnore(int index) {
		return ((DynamicSimConfig) problem.getSimConfig()).getNumIgnore(index);
	}

	public void addDecision(GPIndividual ind, int index, int jobFinished, int decision) {
		if (!shouldSample(index, jobFinished)) {
			return;
		}

		if (!decisionStats.containsKey(ind)) {
			BasicStatistics sc = new BasicStatistics();
			sc.add(decision);
			decisionStats.put(ind, new Pair<BasicStatistics, Integer>(sc, jobFinished));
		} else {
			Pair<BasicStatistics, Integer> stat = decisionStats.get(ind);
			if (stat.b != jobFinished) {
				Pair<BasicStatistics, Integer> newStat = new Pair<BasicStatistics, Integer>(stat.a, jobFinished);
				newStat.a.add(decision);
				decisionStats.put(ind, newStat);
			}
		}
	}

	private boolean shouldSample(int index, int jobFinished) {
		DynamicSimConfig simConfig = (DynamicSimConfig) problem.getSimConfig();

		return (jobFinished >= simConfig.getNumIgnore(index)) &&
				(jobFinished < simConfig.getNumIgnore(index) + SAMPLE_SIZE);
	}

	@Override
	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;
	}

	@Override
	public Pair<GPIndividual, Double>[] getResults() {
		@SuppressWarnings("unchecked")
		Pair<GPIndividual, Double>[] results = new Pair[decisionStats.size()];

		int index = 0;
		for (Map.Entry<GPIndividual, Pair<BasicStatistics, Integer>> entry : decisionStats.entrySet()) {
			GPIndividual ind = entry.getKey();
			BasicStatistics sc = entry.getValue().a;

			results[index++] = new Pair<GPIndividual, Double>(ind, sc.sumSq());
		}

		return results;
	}

	public void clear() {
		decisionStats.clear();
	}

}
