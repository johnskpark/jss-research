package app.evolution.grouped.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.Map;

import app.evolution.grouped.IJasimaGroupFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

public class GroupedSOTWTFitness implements IJasimaGroupFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	@Override
	public void accumulateIndFitness(final Individual ind, final Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			throw new RuntimeException("accumulateIndFitness");
		}

		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		this.ind = ind;
		this.indFitness.combine(stat);
	}

	@Override
	public void accumulateGroupFitness(final Pair<GPIndividual, Double>[] groupResults) {
		// Does nothing.
	}

	@Override
	public void setIndFitness(final EvolutionState state, final Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		KozaFitness fitness = (KozaFitness) ind.fitness;
		fitness.setStandardizedFitness(state, indFitness.mean());
	}

	@Override
	public void setGroupFitness(final EvolutionState state, final GPIndividual[] inds) {
		// Does nothing.
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		setIndFitness(state, ind);
		setGroupFitness(state, new GPIndividual[]{(GPIndividual) ind});
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

	@Override
	public void clear() {
		clearIndFitness();
		clearGroupFitness();
	}

}
