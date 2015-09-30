package app.evolution.multilevel.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.multilevel.IJasimaMultilevelIndividualFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;

/**
 * Fitness calculator for an individual in JasimaMultilevelProblem.
 *
 * Calculates the mean total weighted tardiness (TWT) value to use as the fitness
 * of the individual when the individual is applied to Jasima simulation as a
 * priority-based dispatching rule.
 *
 * @author parkjohn
 *
 */
public class MultilevelIndividualTWTFitness implements IJasimaMultilevelIndividualFitness {

	private static final String WEIGHTED_TARDINESS = "weightedTardMean";

	private SummaryStat overallStat = new SummaryStat();

	@Override
	public void accumulateFitness(int expIndex, Map<String, Object> results) {
		// Results of a simulation over a problem instance.
		SummaryStat stat = (SummaryStat) results.get(WEIGHTED_TARDINESS);

		// We want the total weighted tardiness, so take the
		// sum of the values accumulated by the stats object.
		overallStat.value(stat.sum());
	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		// Set the fitness is the mean total weighted
		// tardiness over the problem instances.
		double indFitness = overallStat.mean() * Math.sqrt(ind.size());

		((KozaFitness) ind.fitness).setStandardizedFitness(state, indFitness);

		ind.evaluated = true;
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}
