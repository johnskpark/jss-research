package ec.multilevel_new;

import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;

public abstract class MLSGroupBreedingPipeline implements Setup {

	private static final long serialVersionUID = -6523109545940475436L;

	public static final String P_PIPE = "pipe";

	public void setup(final EvolutionState state, final Parameter base) {
		// TODO Auto-generated method stub.
	}

	// TODO
	public void prepareToProduce(final EvolutionState state, final int thread) {}

	// TODO
	public void finishProducing(final EvolutionState state, final int thread) {}

	public abstract int typicalIndsProduced();

	public abstract int produce(final EvolutionState state,	final int thread);

	public abstract int produce(final int min,
			final int max,
			final int start,
			final EvolutionState state,
			final int thread);

}
