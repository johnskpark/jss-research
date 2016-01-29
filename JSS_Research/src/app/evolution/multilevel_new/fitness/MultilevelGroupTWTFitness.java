package app.evolution.multilevel_new.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.multilevel_new.IJasimaMultilevelGroupFitness;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;
import ec.multilevel_new.MLSSubpopulation;
import jasima.core.statistics.SummaryStat;

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
public class MultilevelGroupTWTFitness implements IJasimaMultilevelGroupFitness {

	private static final String WEIGHTED_TARDINESS = "weightedTardMean";

	private List<Double> ensembleStat = new ArrayList<Double>();

	@Override
	public List<Double> getInstanceStats() {
		return ensembleStat;
	}

	@Override
	public void accumulateFitness(int expIndex,
			MLSSubpopulation subpop,
			Map<String, Object> results) {
		if (ensembleStat.size() == 0) {
			ensembleStat.add(0.0);
		}

		// Results of a simulation over a problem instance.
		SummaryStat stat = (SummaryStat) results.get(WEIGHTED_TARDINESS);

		double twt = stat.sum();

		// We want the total weighted tardiness, so take the
		// sum of the values accumulated by the stats object.
		ensembleStat.add(twt);
		ensembleStat.set(0, ensembleStat.get(0) + twt);
	}

	@Override
	public void setFitness(EvolutionState state, MLSSubpopulation subpop) {
		double sizeFactor = Math.sqrt((2.0 + subpop.individuals.length) / (2.0 * subpop.individuals.length));
		double groupFitness = ensembleStat.get(0) / (ensembleStat.size() - 1.0) * sizeFactor;

		((KozaFitness) subpop.getFitness()).setStandardizedFitness(state, groupFitness);

		subpop.setEvaluated(true);
	}

	@Override
	public void clear() {
		ensembleStat.clear();
	}

}
