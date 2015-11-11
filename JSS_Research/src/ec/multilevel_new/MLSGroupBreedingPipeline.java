package ec.multilevel_new;

import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;

public abstract class MLSGroupBreedingPipeline implements Setup {

	public static final String P_PIPE = "pipe";

	@Override
	public void setup(final EvolutionState state, final Parameter base) {}
	
	public void prepareToProduce(final EvolutionState state, final int thread) {}

	public void finishProducing(final EvolutionState state, final int thread) {}

	public abstract int typicalIndsProduced();

	public abstract int produce(final EvolutionState state,	final int thread);

	public abstract int produce(final int min,
			final int max,
			final int start,
			final EvolutionState state,
			final int thread);
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(); 
		}
	}

}
