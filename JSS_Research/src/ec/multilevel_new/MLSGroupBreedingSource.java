package ec.multilevel_new;

import ec.EvolutionState;
import ec.Prototype;
import ec.util.Parameter;

public abstract class MLSGroupBreedingSource implements Prototype {

	private static final long serialVersionUID = 7335969303276869118L;

	public void setup(final EvolutionState state, final Parameter base) {}

	public abstract int typicalIndsProduced();

	public abstract int produce(final EvolutionState state,	final int thread);

	public abstract int produce(final int min,
			final int max,
			final int start,
			final EvolutionState state,
			final int thread);
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(); 
		}
	}
	
}
