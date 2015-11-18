package app.evolution.pickardt.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.pickardt.IJasimaPickardtFitness;
import app.evolution.pickardt.JasimaVectorIndividual;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public class PickardtTWTFitness implements IJasimaPickardtFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	@Override
	public void accumulateFitness(int index, Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		overallStat.value(stat.sum());
	}

	@Override
	public void setFitness(EvolutionState state, JasimaVectorIndividual ind) {
		((KozaFitness) ind.fitness).setStandardizedFitness(state, overallStat.mean());
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
