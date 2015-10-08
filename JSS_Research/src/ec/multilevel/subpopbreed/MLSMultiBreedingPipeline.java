package ec.multilevel.subpopbreed;

import ec.EvolutionState;
import ec.multilevel.MLSSubpopBreedingPipeline;
import ec.util.Parameter;

public class MLSMultiBreedingPipeline extends MLSSubpopBreedingPipeline {

	public static final String P_SOURCE = "source";
	public static final String P_PROB = "prob";

	private int numSources;

	private MLSSubpopBreedingPipeline[] sources;
	private double[] probs;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// TODO Auto-generated method stub
	}

	@Override
	public int typicalIndsProduced() {
		// TODO Auto-generated method stub
		return 0;
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
