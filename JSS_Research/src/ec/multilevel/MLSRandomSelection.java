package ec.multilevel;

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
public class MLSRandomSelection extends RandomSelection {

	private static final long serialVersionUID = 4123260959395843573L;

	public static final int INDS_PRODUCED = 1;

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		// TODO Auto-generated method stub.
		return super.produce(subpopulation, state, thread);
	}

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		Subpopulation tempSubpop = ((MLSEvolutionState) state).getTempPopulation().subpops[subpopulation];

		int n = INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		// Get all of the non-null individuals in the inds array.
		List<Pair<Individual, Integer>> nonNullInds = new ArrayList<Pair<Individual, Integer>>();
		for (int i = 0; i < tempSubpop.individuals.length; i++) {
			if (tempSubpop.individuals[i] != null) {
				nonNullInds.add(new Pair<Individual, Integer>(tempSubpop.individuals[i], i));
			}
		}

		// In multilevel, the selection is carried within the individuals array.
		for (int i = 0; i < n; i++) {
			int index = state.random[thread].nextInt(nonNullInds.size());
			inds[start+i] = nonNullInds.get(index).i1;
		}

		return n;
	}

}
