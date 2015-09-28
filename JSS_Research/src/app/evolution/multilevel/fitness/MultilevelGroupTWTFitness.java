package app.evolution.multilevel.fitness;

import jasima.core.statistics.SummaryStat;

import java.util.Map;

import app.evolution.IJasimaTracker;
import app.evolution.multilevel.IJasimaMultilevelGroupFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.multilevel.MLSSubpopulation;

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

// TODO got a bad fitness warning, might want to fix this.
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
		// TODO I'm getting zero group size. This is fucked up shit.
		int groupSize = 0;

		// In the MLSEvaluator, the individuals should have
		// been evaluated, so I can safely get their fitnesses
		// to calculate the fitness of the subpopulation.
		double avgIndFitnesses = 0.0;

		for (int i = 0; i < subpop.individuals.length; i++) {
			if (subpop.individuals[i] == null) {
				continue;
			}

			// Sanity check to ensure that a particular individual has been evaluated.
			if (!subpop.individuals[i].evaluated) {
				state.output.fatal("All non-null individuals in the subpopulation must have been evaluated.");
			}

			KozaFitness fitness = (KozaFitness) subpop.individuals[i].fitness;
			avgIndFitnesses += fitness.standardizedFitness();

			groupSize++;
		}

		// Another sanity check. We can't evaluate a group of size zero.
		if (groupSize == 0) {
			state.output.fatal("Cannot evaluate a subpopulation of size zero.");
		}

		avgIndFitnesses /= groupSize;

		// The final fitness is the linear combination of the
		// average of the individuals fitnesses and the ensemble's
		// fitness, multiplied by a size penalty factor.
		double groupFitness;
		groupFitness = (0.5 * avgIndFitnesses + 0.5 * ensembleStat.mean()) * (groupSize * groupSize);

		((KozaFitness) subpop.getFitness()).setStandardizedFitness(state, groupFitness);

		subpop.setEvaluated(true);
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
