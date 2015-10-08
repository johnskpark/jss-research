package ec.multilevel.subpopbreed;

import ec.EvolutionState;
import ec.multilevel.MLSSubpopBreedingPipeline;
import ec.util.Parameter;

public class MLSCrossoverPipeline extends MLSSubpopBreedingPipeline {

	public static final int TYPICAL_INDS_PRODUCED = 2;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// TODO Auto-generated method stub
	}

	@Override
	public int typicalIndsProduced() {
		return TYPICAL_INDS_PRODUCED;
	}

	@Override
	public int produce(EvolutionState state, int thread) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int produce(int min, int max, int start, EvolutionState state,
			int thread) {
		// TODO Auto-generated method stub
		return 0;
	}

}
