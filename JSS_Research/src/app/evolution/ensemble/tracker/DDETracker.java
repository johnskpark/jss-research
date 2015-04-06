package app.evolution.ensemble.tracker;

import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.ensemble.EnsembleTrackerValue;
import app.util.BasicStatistics;
import ec.gp.GPIndividual;

public class DDETracker extends AbsEnsembleTracker {

	private Map<GPIndividual, Pair<BasicStatistics, Integer>> decisionStats = new HashMap<GPIndividual, Pair<BasicStatistics, Integer>>();

	@Override
	public void addTrackerValue(int jobFinished, EnsembleTrackerValue value) {
		if (!shouldSample(jobFinished)) {
			return;
		}

//		if (!decisionStats.containsKey(ind)) {
//			BasicStatistics sc = new BasicStatistics();
//			sc.add(value);
//			decisionStats.put(ind, new Pair<BasicStatistics, Integer>(sc, jobFinished));
//		} else {
//			Pair<BasicStatistics, Integer> stat = decisionStats.get(ind);
//			if (stat.b != jobFinished) {
//				Pair<BasicStatistics, Integer> newStat = new Pair<BasicStatistics, Integer>(stat.a, jobFinished);
//				newStat.a.add(value);
//				decisionStats.put(ind, newStat);
//			}
//		}
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

	@Override
	public void clear() {
		decisionStats.clear();
	}

}
