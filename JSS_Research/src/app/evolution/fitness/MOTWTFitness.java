package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;

public class MOTWTFitness implements IJasimaFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();
	private SummaryStat overallTrackerStat = new SummaryStat();

	@Override
	public void accumulateFitness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		overallStat.combine(stat);
	}

	@Override
	public void accumulateTrackerFitness(final SummaryStat trackerStat) {
		overallTrackerStat.combine(trackerStat);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		double[] newObjectives = new double[3];
		newObjectives[0] = overallStat.sum();
		newObjectives[1] = ind.size();
		newObjectives[2] = overallTrackerStat.mean();

		((MultiObjectiveFitness) ind.fitness).setObjectives(state, newObjectives);
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
