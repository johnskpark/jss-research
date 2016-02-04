package app.evolution.grouped.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.HashMap;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.grouped.GroupedIndividual;
import app.evolution.grouped.JasimaGroupFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

public class TrialTWTFitness implements JasimaGroupFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	private Map<GPIndividual, SummaryStat> groupFitness = new HashMap<GPIndividual, SummaryStat>();

	@Override
	public void accumulateIndFitness(Individual ind, Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			throw new RuntimeException("accumulateIndFitness");
		}

		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		this.ind = ind;
		this.indFitness.value(stat.sum());
	}

	@Override
	public void accumulateGroupFitness(Individual ind,
			Map<String, Object> results) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFitness(EvolutionState state, JasimaGPIndividual ind) {
		setIndFitness(state, ind);
		setGroupFitness(state, ind, new GroupedIndividual(new GPIndividual[]{(GPIndividual) ind}));
	}

	@Override
	public void setIndFitness(EvolutionState state, Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		KozaFitness fitness = (KozaFitness) ind.fitness;
		fitness.setStandardizedFitness(state, indFitness.mean());

		ind.evaluated = true;
	}

	@Override
	public void setGroupFitness(EvolutionState state, Individual ind,
			GroupedIndividual group) {
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

	@Override
	public void clear() {
		clearIndFitness();
		clearGroupFitness();
	}

}
