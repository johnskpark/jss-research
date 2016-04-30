package app.listener.nguyen_r1;

import app.IWorkStationListener;
import app.evolution.IWorkStationListenerEvolveFactory;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements IWorkStationListenerEvolveFactory {

	private static final long serialVersionUID = -9030410769000603334L;

	private NguyenR1Listener listener = null;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// Does nothing.
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new NguyenR1Listener();
		}
		return listener;
	}

}
