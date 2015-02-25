package app.evolution.pickardt.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.pickardt.IJasimaPickardtFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleFitness;

public class PickardtTWTFitness implements IJasimaPickardtFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	@Override
	public void accumulateFitness(int index, Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		overallStat.value(stat.sum());
	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		((SimpleFitness) ind.fitness).setFitness(state, -overallStat.mean(), false);
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
