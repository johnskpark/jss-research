package app.evolution.coop.tracker;

import jasima.core.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPProblem;
import app.evolution.coop.JasimaCoopProblem;
import ec.gp.GPIndividual;

public class CoopDecisionTrackerOriginal implements IJasimaTracker {

	private static final int SAMPLE_SIZE = 2000;

	private Map<GPIndividual, Pair<List<Double>, Integer>> priorityStats = new HashMap<GPIndividual, Pair<List<Double>, Integer>>();

	private JasimaCoopProblem problem;

	public int getNumIgnore() {
		return problem.getSimConfig().getNumIgnore();
	}

	public void addPriority(GPIndividual ind, int jobFinished, double priority) {
		if (!shouldSample(jobFinished)) {
			return;
		}

		if (!priorityStats.containsKey(ind)) {
			List<Double> stat = new ArrayList<Double>();
			stat.add(priority);
			priorityStats.put(ind, new Pair<List<Double>, Integer>(stat, jobFinished));
		} else {
			Pair<List<Double>, Integer> pair = priorityStats.get(ind);
			if (pair.b != jobFinished) {
				Pair<List<Double>, Integer> newPair = new Pair<List<Double>, Integer>(pair.a, jobFinished);
				newPair.a.add(priority);
				priorityStats.put(ind, newPair);
			}
		}
	}

	private boolean shouldSample(int jobFinished) {
		return (jobFinished >= problem.getSimConfig().getNumIgnore()) &&
				(jobFinished < problem.getSimConfig().getNumIgnore() + SAMPLE_SIZE);
	}

	@Override
	public void setProblem(JasimaGPProblem problem) {
		this.problem = (JasimaCoopProblem) problem;
	}

	@Override
	public Pair<GPIndividual, Double>[] getResults() {
		@SuppressWarnings("unchecked")
		Pair<GPIndividual, Double>[] results = new Pair[priorityStats.size()];

		List<GPIndividual> indList = new ArrayList<GPIndividual>(priorityStats.keySet());
		for (int i = 0; i < indList.size(); i++) {
			results[i] = new Pair<GPIndividual, Double>(indList.get(i), getPenalty(i, indList));
		}

		return results;
	}

	private double getPenalty(int index, List<GPIndividual> indList) {
		GPIndividual ind = indList.get(index);
		List<Double> stat = priorityStats.get(ind).a;
		double penalty = 0.0;

		for (int i = 0; i < indList.size(); i++) {
			if (index == i) {
				continue;
			}
			List<Double> otherStat = priorityStats.get(indList.get(i)).a;
			penalty += getSumSqDiff(stat, otherStat);
		}

		penalty = penalty / (stat.size() * getCoopSize());
		return penalty;
	}

	private double getSumSqDiff(List<Double> stat1, List<Double> stat2) {
		double sumSqDiff = 0.0;

		for (int i = 0; i < stat1.size(); i++) {
			sumSqDiff = (stat1.get(i) - stat2.get(i)) * (stat1.get(i) - stat2.get(i));
		}

		return sumSqDiff;
	}

	private int getCoopSize() {
		return problem.getNumSubpops();
	}

	@Override
	public void clear() {
		priorityStats.clear();
	}

}
