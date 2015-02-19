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

public class CoopTWTFitness implements IJasimaCoopFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Individual[] individuals;

	private Map<Individual, Pair<Integer, SummaryStat[]>> fitnessMap = new HashMap<Individual, Pair<Integer, SummaryStat[]>>();

	@Override
	public void loadIndividuals(final Individual[] inds) {
		for (int i = 0; i < inds.length; i++) {
			addIndividual(inds[i], i);
		}
	}

	private void addIndividual(Individual ind, int index) {
		fitnessMap.put(ind, new Pair<Integer, SummaryStat[]>(index,
				new SummaryStat[]{
				new SummaryStat(),
				new SummaryStat()
		}));
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
		setFitness(state, individuals, ind, true);
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual[] inds,
			final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			setFitness(state, inds, inds[i], shouldSetContext);
		}
	}

	@SuppressWarnings("unchecked")
	private void setFitness(final EvolutionState state,
			final Individual[] inds,
			final Individual ind,
			final boolean shouldSetContext) {
		int index = fitnessMap.get(ind).a;
		SummaryStat[] indStat = fitnessMap.get(ind).b;
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
		fitnessMap.clear();
	}

}
