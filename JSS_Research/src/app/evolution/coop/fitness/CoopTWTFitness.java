package app.evolution.coop.fitness;

import jasima.core.statistics.SummaryStat;
import jasima.core.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.coop.IJasimaCoopFitness;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;

public class CoopTWTFitness implements IJasimaCoopFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual[] individuals;

	// I can't use a map here, as duplicate individuals are a possibility.
	// TODO I can use a map and only count once per individual.
	private List<Pair<Individual, SummaryStat[]>> fitnesses = new ArrayList<Pair<Individual, SummaryStat[]>>();

	@Override
	public void loadIndividuals(final Individual[] inds) {
		if (fitnesses.size() == 0) {
			individuals = new Individual[inds.length];

			for (int i = 0; i < inds.length; i++) {
				individuals[i] = inds[i];

				fitnesses.add(new Pair<Individual, SummaryStat[]>(inds[i], new SummaryStat[]{
						new SummaryStat(),
						new SummaryStat()
				}));
			}
		}
	}

	@Override
	public void accumulateObjectiveFitness(final Individual[] inds,
			final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);
		for (int i = 0; i < inds.length; i++) {
			fitnesses.get(i).b[0].combine(stat);
		}
	}

	@Override
	public void accumulateDiversityFitness(final Pair<GPIndividual, Double>[] groupResults) {
		for (int i = 0; i < groupResults.length; i++) {
			int index = getIndex(groupResults[i].a, i);
			fitnesses.get(index).b[1].value(groupResults[i].b);
		}
	}

	private int getIndex(Individual ind, int startIndex) {
		for (int index = 0; index < fitnesses.size(); index++) {
			if (fitnesses.get((index + startIndex) % fitnesses.size()).a.equals(ind)) {
				return index;
			}
		}
		return -1;
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind) {
		int index = getIndex(ind, 0);

		setFitness(state, individuals, ind, true, index);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			int index = getIndex(inds[i], i);

			setFitness(state, inds, inds[i], shouldSetContext, index);
		}
	}

	@SuppressWarnings("unchecked")
	private void setFitness(final EvolutionState state,
			final Individual[] inds,
			final Individual ind,
			final boolean shouldSetContext,
			final int index) {
		SummaryStat[] indStat = fitnesses.get(index).b;
		double trial = indStat[0].mean();

		MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;

		int len = ind.fitness.trials.size();
		if (len == 0 || (Double) ind.fitness.trials.get(0) < trial) {
			if (shouldSetContext && inds != null) {
				ind.fitness.setContext(inds, index);
			}

			ind.fitness.trials.add(trial);
		}

		fitness.getObjectives()[0] = trial;
		fitness.getObjectives()[1] = ind.size();
		fitness.getObjectives()[2] = -indStat[1].mean();
	}

	@Override
	public void clear() {
		individuals = null;
		fitnesses.clear();
	}

}
