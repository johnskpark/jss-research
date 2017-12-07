package app.evolution.grouped.fitness;

import java.util.HashMap;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.grouped.JasimaGroupFitness;
import app.evolution.grouped.JasimaGroupedIndividual;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import jasima.core.statistics.SummaryStat;

public class TrialTWTFitness extends JasimaGroupFitness {

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	private Map<GPIndividual, SummaryStat> groupFitness = new HashMap<GPIndividual, SummaryStat>();

	@Override
	public void accumulateIndFitness(final int expIndex,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			throw new RuntimeException("accumulateIndFitness");
		}

		double value = getFitness(expIndex, config, ind, results);

		this.ind = ind;
		this.indFitness.value(value);
	}

	@Override
	public double getFitness(final int index,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results) {
		return WeightedTardinessStat.getTotalWeightedTardiness(results);
	}

	@Override
	public void accumulateGroupFitness(final int expIndex,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setIndFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		KozaFitness fitness = (KozaFitness) ind.fitness;
		fitness.setStandardizedFitness(state, getFinalFitness(state, config, ind));

		ind.evaluated = true;
	}

	@Override
	public double getFinalFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind) {
		return indFitness.mean();
	}

	@Override
	public void setGroupFitness(final EvolutionState state,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final JasimaGroupedIndividual group) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearIndFitness() {
		ind = null;
		indFitness.clear();
	}

	@Override
	public void clearGroupFitness() {
		groupFitness.clear();
	}

}
