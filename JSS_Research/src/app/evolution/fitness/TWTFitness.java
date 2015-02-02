package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;

public class TWTFitness implements IJasimaFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat)results.get(WT_MEAN_STR);

		((KozaFitness)ind.fitness).setStandardizedFitness(state, stat.sum());
	}
}
