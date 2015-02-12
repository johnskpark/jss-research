package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaGroupFitness;
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
	public void accumulateIndFitness(Individual ind, Map<String, Object> results) {
		if (this.ind != null && !this.ind.equals(ind)) {
			// TODO throw exception.
		}

		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		this.ind = ind;
		this.indFitness.combine(stat);
	}

	@Override
	public void accumulateGroupFitness(Pair<GPIndividual, Double>[] groupResults) {
		for (Pair<GPIndividual, Double> pair : groupResults) {
			if (!groupFitness.containsKey(pair.a)) {
				groupFitness.put(pair.a, new SummaryStat());
			}
			groupFitness.get(pair.a).value(pair.b);
		}
	}

	@Override
	public void setIndFitness(final EvolutionState state, Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			// TODO throw exception.
		}

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
		fitness.getObjectives()[0] = indFitness.mean();
		fitness.getObjectives()[1] = ind.size();
	}

	@Override
	public void setGroupFitness(final EvolutionState state, GPIndividual[] inds) {
		for (GPIndividual ind : inds) {
			SummaryStat stat = groupFitness.get(ind);
			if (stat == null) {
				// TODO throw exception.
			}

			MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
			fitness.getObjectives()[2] = stat.mean();
		}
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
