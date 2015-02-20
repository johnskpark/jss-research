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
import ec.gp.koza.KozaFitness;

public class CoopSOTWTFitness implements IJasimaCoopFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Map<Individual, Pair<Integer, SummaryStat>> fitnessMap = new HashMap<Individual, Pair<Integer, SummaryStat>>();

	@Override
	public void loadIndividuals(Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			addIndividual(i, inds[i]);
		}
	}

	private void addIndividual(int index, Individual ind) {
		if (!fitnessMap.containsKey(ind)) {
			fitnessMap.put(ind, new Pair<Integer, SummaryStat>(index, new SummaryStat()));
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

			fitnessMap.get(inds[i]).b.combine(stat);
			indSet.add(inds[i]);
		}
	}

	@Override
	public void accumulateDiversityFitness(final Pair<GPIndividual, Double>[] groupResults) {
		// Does nothing.
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
				setFitness(state, inds, inds[i], shouldSetContext);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setFitness(final EvolutionState state,
			final Individual[] inds,
			final Individual ind,
			final boolean shouldSetContext) {
		int index = fitnessMap.get(ind).a;
		SummaryStat indStat = fitnessMap.get(ind).b;
		double trial = indStat.mean();

		KozaFitness fitness = (KozaFitness) ind.fitness;

		int len = ind.fitness.trials.size();
		if (len == 0 || (Double) ind.fitness.trials.get(0) < trial) {
			if (shouldSetContext && inds != null) {
				ind.fitness.setContext(inds, index);
			}

			ind.fitness.trials.add(trial);
		}

		fitness.setStandardizedFitness(state, trial);
	}

	@Override
	public void clear() {
		fitnessMap.clear();
	}

}
