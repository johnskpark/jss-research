package ec.multilevel;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.select.RandomSelection;
import ec.util.Pair;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class MLSRandomSelection extends RandomSelection {

	private static final long serialVersionUID = 4123260959395843573L;

	public static final int INDS_PRODUCED = 1;

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		System.out.println("Start: " + start);

		int n = INDS_PRODUCED;
		if (n < min) n = min;
		if (n > max) n = max;

		// Get all of the non-null individuals in the inds array.
		List<Pair<Individual, Integer>> nonNullInds = new ArrayList<Pair<Individual, Integer>>();
		for (int i = 0; i < start; i++) {
			if (inds[i] != null) {
				nonNullInds.add(new Pair<Individual, Integer>(inds[i], i));
			}
		}

		// In multilevel, the selection is carried within the individuals array.
		for (int i = 0; i < n; i++) {
			int index = state.random[thread].nextInt(nonNullInds.size());
			inds[start+i] = nonNullInds.get(index).i1;
		}

		return 0;
	}

}
