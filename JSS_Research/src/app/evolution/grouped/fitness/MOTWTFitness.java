package app.evolution.grouped.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.grouped.IJasimaGroupFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;

public class MOTWTFitness implements IJasimaGroupFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual ind;
	private SummaryStat indFitness = new SummaryStat();

	private Map<GPIndividual, SummaryStat> groupFitness = new HashMap<GPIndividual, SummaryStat>();

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
		for (Pair<GPIndividual, Double> pair : groupResults) {
			if (!groupFitness.containsKey(pair.a)) {
				groupFitness.put(pair.a, new SummaryStat());
			}
			groupFitness.get(pair.a).value(pair.b);
		}
	}

	@Override
	public void setIndFitness(final EvolutionState state, final Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
		fitness.getObjectives()[0] = indFitness.mean();
		fitness.getObjectives()[1] = ind.size();
	}

	@Override
	public void setGroupFitness(final EvolutionState state, final GPIndividual[] inds) {
		for (GPIndividual ind : inds) {
			SummaryStat stat = groupFitness.get(ind);
			if (stat == null) {
				throw new RuntimeException("setGroupFitness");
			}

			MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
			fitness.getObjectives()[2] = -stat.mean();
		}
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
		groupFitness.clear();
	}

	@Override
	public void clear() {
		clearIndFitness();
		clearGroupFitness();
	}

}
