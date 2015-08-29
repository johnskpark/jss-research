package app.evolution.multilevel.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaTracker;
import app.evolution.multilevel.IJasimaMultilevelGroupFitness;
import app.evolution.multilevel.MLSSubpopulation;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MultilevelGroupTWTFitness implements IJasimaMultilevelGroupFitness {

	private static final String WEIGHTED_TARDINESS = "weightedTardMean";

	private SummaryStat ensembleStat = new SummaryStat();

	@Override
	public void accumulateFitness(int expIndex,
			MLSSubpopulation subpop,
			Map<String, Object> results,
			IJasimaTracker tracker) {
		// Results of a simulation over a problem instance.
		SummaryStat stat = (SummaryStat) results.get(WEIGHTED_TARDINESS);

		// We want the total weighted tardiness, so take the
		// sum of the values accumulated by the stats object.
		ensembleStat.value(stat.sum());
	}

	@Override
	public void setFitness(EvolutionState state,
			MLSSubpopulation subpop,
			boolean[] updateFitness,
			boolean shouldSetContext) {
		int groupSize = subpop.individuals.length;

		// In the MLSEvaluator, the individuals should have
		// been evaluated, so I can safely get their fitnesses
		// to calculate the fitness of the subpopulation.
		double avgIndFitnesses = 0.0;

		for (int i = 0; i < groupSize; i++) {
			assert subpop.individuals[i].evaluated;

			KozaFitness fitness = (KozaFitness) subpop.individuals[i].fitness;
			avgIndFitnesses += fitness.standardizedFitness();
		}

		avgIndFitnesses /= groupSize;

		// The final fitness is the linear combination of the
		// average of the individuals fitnesses and the ensemble's
		// fitness, multiplied by a size penalty factor.
		double groupFitness;
		groupFitness = (0.5 * avgIndFitnesses + 0.5 * ensembleStat.mean());
		groupFitness *= Math.sqrt((2.0 + groupSize) / (2.0 * groupSize));

		((KozaFitness) subpop.getFitness()).setStandardizedFitness(state, groupFitness);
	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		// FIXME Does nothing at the moment.
	}

	@Override
	public void clear() {
		ensembleStat.clear();
	}

}
