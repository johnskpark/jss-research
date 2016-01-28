package app.evolution.multilevel_new.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.multilevel_new.IJasimaMultilevelIndividualFitness;
import app.evolution.multilevel_new.JasimaMultilevelIndividual;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import jasima.core.statistics.SummaryStat;

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
public class MLGPIndividualNormTWTFitness implements IJasimaMultilevelIndividualFitness {

	private static final String WEIGHTED_TARDINESS = "weightedTardMean";

	private List<Double> overallStat = new ArrayList<Double>();

	@Override
	public void accumulateFitness(Individual ind,
			int expIndex,
			Map<String, Object> results,
			double referenceStat) {
		if (overallStat.size() == 0) {
			overallStat.add(0.0);
		}

		// Results of a simulation over a problem instance.
		SummaryStat stat = (SummaryStat) results.get(WEIGHTED_TARDINESS);

		double twt = stat.sum();
		double normTWT = 1.0 * twt / referenceStat;

		// We want the total weighted tardiness, so take the
		// sum of the values accumulated by the stats object.
		overallStat.add(normTWT);

		overallStat.set(0, overallStat.get(0) + normTWT);
	}

	@Override
	public void setFitness(EvolutionState state, JasimaMultilevelIndividual ind) {
		// Set the fitness is the mean total weighted
		// tardiness over the problem instances.
		double indFitness = overallStat.get(0) / (overallStat.size() - 1.0);

		((KozaFitness) ind.fitness).setStandardizedFitness(state, indFitness);

		ind.evaluated = true;
	}

	@Override
	public void clear() {
		overallStat.clear();
	}

}