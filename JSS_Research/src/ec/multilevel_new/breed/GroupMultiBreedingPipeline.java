package ec.multilevel_new.breed;

import ec.EvolutionState;
import ec.multilevel_new.MLSDefaults;
import ec.multilevel_new.MLSGroupBreedingPipeline;
import ec.util.Parameter;

public class GroupMultiBreedingPipeline implements MLSGroupBreedingPipeline {

	public static final String P_NUMSOURCES = "num-sources";
	public static final String P_SOURCE = "source";

	public static final String P_PROB = "prob";

	public static final String P_MULTIBREED = "multibreed";

	public static final int DYNAMIC_SOURCES = 0;

	private Parameter myBase;

	private MLSGroupBreedingPipeline[] sources;
	private double[] probs;

	public Parameter defaultBase() {
		return MLSDefaults.base().push(P_MULTIBREED);
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		myBase = base;

		Parameter def = defaultBase();

		// Load in the sources.
		int numSources = state.parameters.getInt(base.push(P_NUMSOURCES), def.push(P_NUMSOURCES), 1);
		if (numSources == 0) {
			state.output.fatal("Breeding pipeline num-sources value must be > 0",
					base.push(P_NUMSOURCES),
					def.push(P_NUMSOURCES));
		}

		sources = new MLSGroupBreedingPipeline[numSources];

		for (int i = 0; i < sources.length; i++) {
			
		}
	}

	@Override
	public void prepareToProduce(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishProducing(EvolutionState state, int thread) {
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
