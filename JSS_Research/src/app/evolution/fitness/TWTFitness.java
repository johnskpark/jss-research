package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.GroupResult;
import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;

public class TWTFitness implements IJasimaFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	@Override
	public void accumulateFitness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat)results.get(WT_MEAN_STR);

		overallStat.combine(stat);
	}

	@Override
	public void accumulateTrackerFitness(final GroupResult[] trackerResults) {
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		((KozaFitness)ind.fitness).setStandardizedFitness(state, overallStat.sum());
	}

	@Override
	public void clearFitness() {
		overallStat.clear();
	}

	@Override
	public void clearTrackerFitness() {
	}

}
