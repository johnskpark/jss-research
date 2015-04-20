package app.evolution.grouped.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.evolution.grouped.IJasimaGroupFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;

// TODO need to fix.
public class TrialMOTWTFitness implements IJasimaGroupFitness {

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
		this.indFitness.value(stat.sum());
	}

	@Override
	public void accumulateGroupFitness(final Individual ind,
			final Map<String, Object> results,
			final Pair<GPIndividual, Double>[] groupResults) {
		accumulateIndFitness(ind, results);

		// TODO need to fix.
		Set<Individual> indSet = new HashSet<Individual>();
		for (Pair<GPIndividual, Double> result : groupResults) {
			if (indSet.contains(result.a)) {
				continue;
			}

			if (!groupFitness.containsKey(result.a)) {
				groupFitness.put(result.a, new SummaryStat());
			}

			groupFitness.get(result.a).value(result.b);
			indSet.add(result.a);
		}
	}

	@Override
	public void setIndFitness(final EvolutionState state, final Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
		fitness.getObjectives()[0] = indFitness.sum();
		fitness.getObjectives()[1] = ind.size();
	}

	@Override
	public void setGroupFitness(final EvolutionState state,
			final Individual ind,
			final GPIndividual[] inds) {
		for (GPIndividual i : inds) {
			SummaryStat stat = groupFitness.get(i);
			if (stat == null) {
				throw new RuntimeException("setGroupFitness");
			}

			MultiObjectiveFitness fitness = (MultiObjectiveFitness) i.fitness;
			fitness.getObjectives()[2] = -stat.mean();
		}
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		setIndFitness(state, ind);
		setGroupFitness(state, ind, new GPIndividual[]{(GPIndividual) ind});
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
