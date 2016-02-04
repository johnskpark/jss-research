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
import ec.multiobjective.MultiObjectiveFitness;

public class GroupedMOTWTFitness implements JasimaGroupFitness {

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
			final Map<String, Object> results) {
//		Set<Individual> indSet = new HashSet<Individual>();
//		for (Pair<GPIndividual, Double> result : groupResults) {
//			if (indSet.contains(result.a)) {
//				continue;
//			}
//
//			if (!groupFitness.containsKey(result.a)) {
//				groupFitness.put(result.a, new SummaryStat());
//			}
//
//			groupFitness.get(result.a).value(result.b);
//			indSet.add(result.a);
//		}
	}

	@Override
	public void setIndFitness(final EvolutionState state, final Individual ind) {
		if (this.ind == null || !this.ind.equals(ind)) {
			throw new RuntimeException("setIndFitness");
		}

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
		fitness.getObjectives()[0] = indFitness.sum();
		fitness.getObjectives()[1] = ind.size();

		ind.evaluated = true;
	}

	@Override
	public void setGroupFitness(final EvolutionState state,
			final Individual ind,
			final GroupedIndividual group) {
		for (GPIndividual i : group.getInds()) {
			SummaryStat stat = groupFitness.get(i);
			if (stat == null) {
				throw new RuntimeException("setGroupFitness");
			}

			MultiObjectiveFitness fitness = (MultiObjectiveFitness) i.fitness;
			fitness.getObjectives()[2] = -stat.mean();

			group.setEvaluated(true);
		}
	}

	@Override
	public void setFitness(final EvolutionState state,
			final JasimaGPIndividual ind) {
		setIndFitness(state, ind);
		setGroupFitness(state, ind, new GroupedIndividual(new GPIndividual[]{(GPIndividual) ind}));
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
