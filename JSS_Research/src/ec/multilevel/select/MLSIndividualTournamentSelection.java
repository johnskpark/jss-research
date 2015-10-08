package ec.multilevel.select;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.multilevel.MLSEvolutionState;
import ec.select.TournamentSelection;
import ec.util.Pair;


public class MLSIndividualTournamentSelection extends TournamentSelection {

	private static final long serialVersionUID = 6365227684890903499L;

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		List<Pair<Individual, Integer>> nonNullInds = getNonNullInds(state, subpopulation);
		int s = getTournamentSizeToUse(state.random[thread]);

		// In multilevel, the selection is carried within the temporary population.
		Pair<Individual, Integer> best = selectIndividual(s, nonNullInds, subpopulation, state, thread);

		return best.i2;
	}

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		List<Pair<Individual, Integer>> nonNullInds = getNonNullInds(state, subpopulation);

		int n = INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		int s = getTournamentSizeToUse(state.random[thread]);

		// In multilevel, the selection is carried within the temporary population.
		for (int i = 0; i < n; i++) {
			Pair<Individual, Integer> best = selectIndividual(s, nonNullInds, subpopulation, state, thread);

			inds[start+i] = best.i1;
		}

		return n;
	}

	private Pair<Individual, Integer> selectIndividual(int selectionSize,
			List<Pair<Individual, Integer>> nonNullInds,
			int subpopulation,
			EvolutionState state,
			int thread) {
		Pair<Individual, Integer> best = getRandomIndividual(0, nonNullInds, state, thread);

		for (int i = 1; i < selectionSize; i++) {
			Pair<Individual, Integer> ind = getRandomIndividual(i, nonNullInds, state, thread);

			if (pickWorst) {
				if (!betterThan(ind.i1, best.i1, subpopulation, state, thread)) {
					best = ind;
				}
			} else {
				if (betterThan(ind.i1, best.i1, subpopulation, state, thread)) {
					best = ind;
				}
			}
		}

		return best;
	}

	private Pair<Individual, Integer> getRandomIndividual(int number,
			List<Pair<Individual, Integer>> nonNullInds,
			EvolutionState state,
			int thread) {
		int index = state.random[thread].nextInt(nonNullInds.size());

		return nonNullInds.get(index);
	}

	private List<Pair<Individual, Integer>> getNonNullInds(EvolutionState state, int subpopulation) {
		Subpopulation tempSubpop = ((MLSEvolutionState) state).getTempPopulation().subpops[subpopulation];

		// Get all of the non-null individuals in the inds array.
		List<Pair<Individual, Integer>> nonNullInds = new ArrayList<Pair<Individual, Integer>>();
		for (int i = 0; i < tempSubpop.individuals.length; i++) {
			if (tempSubpop.individuals[i] != null) {
				nonNullInds.add(new Pair<Individual, Integer>(tempSubpop.individuals[i], i));
			}
		}

		return nonNullInds;
	}

}
