package app.evolution.simple.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import app.evolution.simple.IJasimaSimpleFitness;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;
import jasima.core.statistics.SummaryStat;

public class TWTFitness implements IJasimaSimpleFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private List<Double> overallStat = new ArrayList<Double>();

	@Override
	public void setProblem(JasimaGPProblem problem) {
		// Do nothing.
	}

	@Override
	public void accumulateFitness(final int index, final Map<String, Object> results) {
		if (overallStat.size() == 0) {
			overallStat.add(0.0);
		}

		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		double twt = stat.sum();

		overallStat.add(twt);
		overallStat.set(0, overallStat.get(0) + twt);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final JasimaGPIndividual ind) {
		((KozaFitness) ind.fitness).setStandardizedFitness(state, overallStat.get(0) / (overallStat.size() + 1.0));
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
