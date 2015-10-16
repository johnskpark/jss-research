package app.evolution.coop.tracker;

import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPProblem;
import app.util.BasicStatistics;
import ec.gp.GPIndividual;

public class CoopDecisionTracker implements IJasimaTracker {

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
			BasicStatistics stat = new BasicStatistics();
			stat.add(decision);
			decisionStats.put(ind, new Pair<BasicStatistics, Integer>(stat, jobFinished));
		} else {
			Pair<BasicStatistics, Integer> pair = decisionStats.get(ind);
			if (pair.b != jobFinished) {
				Pair<BasicStatistics, Integer> newPair = new Pair<BasicStatistics, Integer>(pair.a, jobFinished);
				newPair.a.add(decision);
				decisionStats.put(ind, newPair);
			}
		}
	}

	private boolean shouldSample(int jobFinished) {
		return (jobFinished >= problem.getSimConfig().getNumIgnore()) &&
				(jobFinished < problem.getSimConfig().getNumIgnore() + SAMPLE_SIZE);
	}

	@Override
	public void setProblem(IJasimaGPProblem problem) {
		this.problem = problem;
	}

	public void setProblem(JasimaGPProblem problem) {
		// TODO
	}

	@Override
	public Pair<GPIndividual, Double>[] getResults() {
		@SuppressWarnings("unchecked")
		Pair<GPIndividual, Double>[] results = new Pair[decisionStats.size()];

		int index = 0;
		for (Map.Entry<GPIndividual, Pair<BasicStatistics, Integer>> entry : decisionStats.entrySet()) {
			GPIndividual ind = entry.getKey();
			BasicStatistics stat = entry.getValue().a;

			results[index++] = new Pair<GPIndividual, Double>(ind, stat.sumSq());
		}

		return results;
	}

	@Override
	public void clear() {
		decisionStats.clear();
	}

}
