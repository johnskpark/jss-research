package app.evolution.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;

public class MOTWTFitness implements IJasimaFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat)results.get(WT_MEAN_STR);

		// TODO two fitness for now, need to add in the diversity measure.
		double[] newObjectives = new double[2];
		newObjectives[0] = stat.sum();
		newObjectives[1] = ind.size();

		((MultiObjectiveFitness)ind.fitness).setObjectives(state, newObjectives);
	}
}
