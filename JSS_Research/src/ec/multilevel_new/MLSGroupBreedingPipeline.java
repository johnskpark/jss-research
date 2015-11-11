package ec.multilevel_new;

import ec.EvolutionState;
import ec.Setup;

public interface MLSGroupBreedingPipeline extends Setup {

	public static final String P_PIPE = "pipe";

	public void prepareToProduce(final EvolutionState state, final int thread);

	public void finishProducing(final EvolutionState state, final int thread);

	public int typicalIndsProduced();

	public int produce(final EvolutionState state,	final int thread);

	public int produce(final int min,
			final int max,
			final int start,
			final EvolutionState state,
			final int thread);

}
