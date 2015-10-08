package ec.multilevel;

import ec.EvolutionState;
import ec.Individual;
import ec.SelectionMethod;
import ec.util.Parameter;

// TODO this acts as a wrapper around either random or tournament selection.
// TODO I probably won't need this.
public class MLSSubpopSelection extends SelectionMethod {

	private static final long serialVersionUID = 3810191251873503242L;

	public static final String P_MLSSELECT = "select";

	@Override
	public Parameter defaultBase() {
		return MLSDefaults.base().push(P_MLSSELECT);
	}

	@Override
	public void prepareToProduce(final EvolutionState state,
			final int subpopulation,
			final int thread) {
		// TODO Auto-generated method stub
	}

	@Override
	public int produce(final int subpopulation,
			final EvolutionState state,
			final int thread) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		// TODO Auto-generated method stub
		return 0;
	}
}
