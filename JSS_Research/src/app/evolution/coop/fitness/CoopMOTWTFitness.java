package app.evolution.coop.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.evolution.coop.IJasimaCoopFitness;
import app.evolution.coop.JasimaCoopIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;

public class CoopMOTWTFitness implements IJasimaCoopFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Map<Individual, Pair<Integer, SummaryStat[]>> fitnessMap = new HashMap<Individual, Pair<Integer, SummaryStat[]>>();

	@Override
	public void loadIndividuals(Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			addIndividual(i, inds[i]);
		}
	}

	private void addIndividual(int index, Individual ind) {
		if (!fitnessMap.containsKey(ind)) {
			fitnessMap.put(ind, new Pair<Integer, SummaryStat[]>(index, new SummaryStat[]{
					new SummaryStat(),
					new SummaryStat()
			}));
		}
	}

	@Override
	public void accumulateObjectiveFitness(final Individual[] inds,
			final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);

		Set<Individual> indSet = new HashSet<Individual>();
		for (int i = 0; i < inds.length; i++) {
			if (indSet.contains(inds[i])) {
				continue;
			}

			fitnessMap.get(inds[i]).b[0].value(stat.sum());
			indSet.add(inds[i]);
		}
	}

	@Override
	public void setFitness(final EvolutionState state,
			final JasimaCoopIndividual ind) {
		setTrialFitness(state, new Individual[]{ind}, new boolean[]{true}, true);
		setDiversityFitness(state, new Individual[]{ind}, new boolean[]{true});
		setObjectiveFitness(state, new Individual[]{ind});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTrialFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness,
			final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			if (updateFitness[i]) {
				SummaryStat[] indFitness = fitnessMap.get(inds[i]).b;
				double trial = indFitness[0].mean();

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
	public void setDiversityFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness) {
		for (int i = 0; i < inds.length; i++) {
			if (updateFitness[i]) {
				SummaryStat[] indFitness = fitnessMap.get(inds[i]).b;

				MultiObjectiveFitness fitness = (MultiObjectiveFitness) inds[i].fitness;

				fitness.getObjectives()[1] = inds[i].size();
				fitness.getObjectives()[2] = -indFitness[1].mean();
			}
		}
	}

	@Override
	public void setObjectiveFitness(final EvolutionState state,
			final Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			MultiObjectiveFitness fitness = (MultiObjectiveFitness) inds[i].fitness;

			// we take the minimum over the trials
			double min = Double.POSITIVE_INFINITY;
			for (int l = 0; l < fitness.trials.size(); l++) {
				double trialVal = (Double) fitness.trials.get(l);
				min = Math.min(trialVal, min);  // it'll be the first one, but whatever
			}

			fitness.getObjectives()[0] = min;
			inds[i].evaluated = true;
		}
	}

	@Override
	public void clear() {
		fitnessMap.clear();
	}

}
