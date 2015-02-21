package app.evolution.simple.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaGPProblem;
import app.evolution.simple.IJasimaSimpleFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;

public class TWTFitness implements IJasimaSimpleFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	@Override
	public void setProblem(IJasimaGPProblem problem) {
		// Do nothing.
	}
	
	@Override
	public void accumulateFitness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		overallStat.value(stat.sum());
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		((KozaFitness) ind.fitness).setStandardizedFitness(state, overallStat.mean());
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
