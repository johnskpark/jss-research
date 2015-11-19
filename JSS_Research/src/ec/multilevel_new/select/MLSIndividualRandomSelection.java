package ec.multilevel_new.select;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.select.RandomSelection;
import ec.util.Pair;

/**
 * A method of randomly selecting individuals for breeding adapted for MLSBreeder.
 *
 * @author parkjohn
 *
 */
// TODO need to use the coopPopulation here instead.
public class MLSIndividualRandomSelection extends RandomSelection {

	private static final long serialVersionUID = 4123260959395843573L;

	public static final int INDS_PRODUCED = 1;

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		List<Pair<Individual, Integer>> nonNullInds = getNonNullInds(state, subpopulation);

		int index = state.random[thread].nextInt(nonNullInds.size());

		return nonNullInds.get(index).i2;
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

		// In multilevel, the selection is carried within the individuals array.
		for (int i = 0; i < n; i++) {
			int index = state.random[thread].nextInt(nonNullInds.size());
			inds[start+i] = nonNullInds.get(index).i1;
		}

		return n;
	}

	private List<Pair<Individual, Integer>> getNonNullInds(EvolutionState state, int subpopulation) {
		// TODO
//		Subpopulation tempSubpop = ((MLSEvolutionState) state).getTempPopulation().subpops[subpopulation];
		Subpopulation tempSubpop = null;

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
