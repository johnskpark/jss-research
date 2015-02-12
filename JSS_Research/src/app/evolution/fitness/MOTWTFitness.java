package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;

public class MOTWTFitness implements IJasimaFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	private Map<Individual, SummaryStat> overallTrackerStat = new HashMap<Individual, SummaryStat>();

	@Override
	public void accumulateFitness(final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		overallStat.combine(stat);
	}

	@Override
	public void accumulateTrackerFitness(final Pair<GPIndividual, Double>[] trackerResults) {
		for (Pair<GPIndividual, Double> result : trackerResults) {
			GPIndividual ind = result.a;
			double fitness = result.b;

			if (!overallTrackerStat.containsKey(ind)) {
				overallTrackerStat.put(ind, new SummaryStat());
			}
			overallTrackerStat.get(ind).value(fitness);
		}
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		double[] newObjectives = new double[3];
		newObjectives[0] = overallStat.sum();
		newObjectives[1] = ind.size();
		newObjectives[2] = overallTrackerStat.get(ind).mean();

		((MultiObjectiveFitness) ind.fitness).setObjectives(state, newObjectives);
	}

	@Override
	public void clearFitness() {
		overallStat.clear();
	}

	@Override
	public void clearTrackerFitness() {
		overallTrackerStat.clear();
	}

}
