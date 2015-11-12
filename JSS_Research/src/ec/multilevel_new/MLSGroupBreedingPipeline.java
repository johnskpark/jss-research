package ec.multilevel_new;

import ec.EvolutionState;
import ec.util.Parameter;

public abstract class MLSGroupBreedingPipeline extends MLSGroupBreedingSource {

	private static final long serialVersionUID = 9174478600247349828L;
	
	public static final String P_PIPE = "pipe";

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
	}
	
	public void prepareToProduce(final EvolutionState state, final int thread) {}

	public void finishProducing(final EvolutionState state, final int thread) {}

	@Override
	public Object clone() {
		MLSGroupBreedingPipeline breedingPipeline = (MLSGroupBreedingPipeline) super.clone();
		
		return breedingPipeline;
	}

}
