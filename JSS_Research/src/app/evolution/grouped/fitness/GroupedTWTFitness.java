package app.evolution.grouped.fitness;

import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.grouped.JasimaGroupFitness;
import app.evolution.grouped.JasimaGroupedIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import jasima.core.statistics.SummaryStat;

public class GroupedTWTFitness extends JasimaGroupFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	@Override
	public void accumulateIndFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			throw new RuntimeException("accumulateIndFitness");
		}

		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		this.ind = ind;
		this.indFitness.value(stat.sum());
	}

	@Override
	public void accumulateGroupFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		// Does nothing.
	}

	@Override
	public void setIndFitness(EvolutionState state, JasimaGPIndividual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		KozaFitness fitness = (KozaFitness) ind.fitness;
		fitness.setStandardizedFitness(state, indFitness.mean());
	}

	@Override
	public void setGroupFitness(EvolutionState state, JasimaGPIndividual ind, JasimaGroupedIndividual group) {
		// Does nothing.
	}

	@Override
	public void clearIndFitness() {
		ind = null;
		indFitness.clear();
	}

	@Override
	public void clearGroupFitness() {
		// Does nothing.
	}

}
