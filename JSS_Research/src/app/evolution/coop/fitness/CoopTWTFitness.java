package app.evolution.coop.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.evolution.coop.IJasimaCoopFitness;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;

public class CoopTWTFitness implements IJasimaCoopFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Map<Individual, SummaryStat> fitnessMap = new HashMap<Individual, SummaryStat>();

	@Override
	public void loadIndividuals(Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			if (!fitnessMap.containsKey(inds[i])) {
				fitnessMap.put(inds[i], new SummaryStat());
			}
		}
	}

	@Override
	public void accumulateObjectiveFitness(Individual[] inds,
			Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		Set<Individual> indSet = new HashSet<Individual>();
		for (int i = 0; i < inds.length; i++) {
			if (indSet.contains(inds[i])) {
				continue;
			}

			fitnessMap.get(inds[i]).value(stat.sum());
			indSet.add(inds[i]);
		}
	}

	@Override
	public void setFitness(EvolutionState state, Individual ind) {
		setTrialFitness(state, new Individual[]{ind}, new boolean[]{true}, true);
		setDiversityFitness(state, new Individual[]{ind}, new boolean[]{true});
		setObjectiveFitness(state, new Individual[]{ind});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTrialFitness(EvolutionState state, Individual[] inds,
			boolean[] updateFitness, boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			if (updateFitness[i]) {
				SummaryStat indFitness = fitnessMap.get(inds[i]);
				double trial = indFitness.mean();

				Fitness fitness = inds[i].fitness;

				int len = fitness.trials.size();
				if (len == 0 || (Double) fitness.trials.get(0) > trial) {
					if (shouldSetContext) {
						fitness.setContext(inds, i);
					}

					fitness.trials.add(0, trial);
				}
			}
		}
	}

	@Override
	public void setDiversityFitness(EvolutionState state, Individual[] inds,
			boolean[] updateFitness) {
		// Diversity measure does nothing.
	}

	@Override
	public void setObjectiveFitness(EvolutionState state, Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			KozaFitness fitness = (KozaFitness) inds[i].fitness;

			// we take the minimum over the trials
			double min = Double.POSITIVE_INFINITY;
			for (int l = 0; l < fitness.trials.size(); l++) {
				double trialVal = (Double) fitness.trials.get(l);
				min = Math.min(trialVal, min);  // it'll be the first one, but whatever
			}

			fitness.setStandardizedFitness(state, min);
			inds[i].evaluated = true;
		}
	}

	@Override
	public void clear() {
		fitnessMap.clear();
	}

}
