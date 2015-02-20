package app.evolution.coop.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.evolution.coop.IJasimaCoopFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
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

			fitnessMap.get(inds[i]).b[0].combine(stat);
			indSet.add(inds[i]);
		}
	}

	@Override
	public void accumulateDiversityFitness(final Pair<GPIndividual, Double>[] groupResults) {
		Set<Individual> indSet = new HashSet<Individual>();
		for (int i = 0; i < groupResults.length; i++) {
			Pair<GPIndividual, Double> result = groupResults[i];
			if (indSet.contains(result.a)) {
				continue;
			}

			fitnessMap.get(result.a).b[1].value(result.b);
			indSet.add(result.a);
		}
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		throw new UnsupportedOperationException("Not yet implemented");
		// setFitness(state, individuals, 0, true);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness,
			final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			if (updateFitness[i]) {
				setFitness(state, inds, i, shouldSetContext);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setFitness(final EvolutionState state,
			final Individual[] inds,
			final int index,
			final boolean shouldSetContext) {
		SummaryStat[] indStat = fitnessMap.get(inds[index]).b;
		double trial = indStat[0].mean();

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) inds[index].fitness;

		int len = inds[index].fitness.trials.size();
		if (len == 0 || (Double) inds[index].fitness.trials.get(0) < trial) {
			if (shouldSetContext) {
				inds[index].fitness.setContext(inds, index);
			}

			inds[index].fitness.trials.add(trial);
		}

		fitness.getObjectives()[0] = trial;
		fitness.getObjectives()[1] = inds[index].size();
		fitness.getObjectives()[2] = -indStat[1].mean();
	}

	@Override
	public void clear() {
		fitnessMap.clear();
	}

}
