package app.evolution.multilevel_new.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.multilevel_new.IJasimaMultilevelGroupFitness;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;
import ec.multilevel_new.MLSSubpopulation;

/**
 * Fitness calculator for a group of individuals in JasimaMultilevelProblem.
 *
 * Calculates the mean total weighted tardiness (TWT) value of the group when
 * applied to Jasima simulations as an ensemble, and combines it with the
 * size of the group and the individuals' fitnesses to calculate a fitness
 * value for the group as a whole.
 *
 * @author parkjohn
 *
 */

// TODO this gives terrible results, try to figure out why this is the case.
public class MultilevelGroupTWTFitness implements IJasimaMultilevelGroupFitness {

	private static final String WEIGHTED_TARDINESS = "weightedTardMean";

	private SummaryStat ensembleStat = new SummaryStat();

	@Override
	public void accumulateFitness(int expIndex,
			MLSSubpopulation subpop,
			Map<String, Object> results) {
		// Results of a simulation over a problem instance.
		SummaryStat stat = (SummaryStat) results.get(WEIGHTED_TARDINESS);

		// We want the total weighted tardiness, so take the
		// sum of the values accumulated by the stats object.
		ensembleStat.value(stat.mean());
	}

	@Override
	public void setFitness(EvolutionState state, MLSSubpopulation subpop) {
		double sizeFactor = Math.sqrt((2.0 + subpop.individuals.length) / (2.0 * subpop.individuals.length));
		double groupFitness = ensembleStat.mean() * sizeFactor;

		((KozaFitness) subpop.getFitness()).setStandardizedFitness(state, groupFitness);

		subpop.setEvaluated(true);
	}

	@Override
	public void clear() {
		ensembleStat.clear();
	}

}
