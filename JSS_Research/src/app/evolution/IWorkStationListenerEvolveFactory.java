package app.evolution;

import app.listener.IWorkStationListenerFactory;
import ec.EvolutionState;
import ec.util.Parameter;

public interface IWorkStationListenerEvolveFactory extends IWorkStationListenerFactory {

	// FIXME Integrate this with the setup from ec.
	public void setup(final EvolutionState state, final Parameter base);

}
