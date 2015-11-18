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
import ec.gp.koza.KozaFitness;

public class CoopPriorityFitness implements IJasimaCoopFitness {

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
	public void accumulateObjectiveFitness(Individual[] inds,
			Map<String, Object> results) {
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
	public void setFitness(EvolutionState state, JasimaCoopIndividual ind) {
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
				SummaryStat[] indFitness = fitnessMap.get(inds[i]).b;
				double trial = getFinalFitness(indFitness);

				Fitness fitness = inds[i].fitness;

				int len = fitness.trials.size();
				if (len == 0 || (Double) fitness.trials.get(0) < trial) {
					if (shouldSetContext) {
						fitness.setContext(inds, i);
					}

					fitness.trials.add(trial);
				}
			}
		}
	}

	private double getFinalFitness(SummaryStat[] fitness) {
		return fitness[0].mean() * (1 + fitness[1].mean());
	}

	@Override
	public void setDiversityFitness(EvolutionState state, Individual[] inds,
			boolean[] updateFitness) {
		// Does nothing, since this is single objective.
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
