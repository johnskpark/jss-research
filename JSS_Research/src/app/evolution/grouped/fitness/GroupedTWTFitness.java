package app.evolution.grouped.fitness;

import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.grouped.JasimaGroupFitness;
import app.evolution.grouped.JasimaGroupedIndividual;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import jasima.core.statistics.SummaryStat;

public class GroupedTWTFitness extends JasimaGroupFitness {

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	@Override
	public void accumulateIndFitness(int expIndex, JasimaGPIndividual ind, Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			throw new RuntimeException("accumulateIndFitness");
		}

		double value = getFitness(expIndex, ind, results);

		this.ind = ind;
		this.indFitness.value(value);
	}

	@Override
	public double getFitness(final int index, final JasimaGPIndividual ind, final Map<String, Object> results) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
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
		fitness.setStandardizedFitness(state, getFinalFitness(state, ind));
	}

	@Override
	public double getFinalFitness(final EvolutionState state, JasimaGPIndividual ind) {
		return indFitness.mean();
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
