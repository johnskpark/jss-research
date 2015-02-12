package app.evolution.tracker;

import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import app.util.BasicStatistics;
import ec.gp.GPIndividual;

public class DecisionTracker implements IJasimaTracker {

	private static final int SAMPLE_SIZE = 2000;

	private Map<GPIndividual, Pair<BasicStatistics, Integer>> decisionStats = new HashMap<GPIndividual, Pair<BasicStatistics, Integer>>();

	private IJasimaGPProblem problem;

	public int getNumIgnore() {
		return problem.getSimConfig().getNumIgnore();
	}

	public void addDecision(GPIndividual ind, int jobFinished, int decision) {
		if (!shouldSample(jobFinished)) {
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

	private boolean shouldSample(int jobFinished) {
		return (jobFinished >= problem.getSimConfig().getNumIgnore()) &&
				(jobFinished < problem.getSimConfig().getNumIgnore() + SAMPLE_SIZE);
	}

	public void setProblem(IJasimaGPProblem problem) {
		this.problem = problem;
	}

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
