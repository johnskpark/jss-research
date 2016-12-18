package app.listener.nguyen_r1;

import app.JasimaWorkStationListener;
import app.evolution.JasimaWorkStationListenerEvolveFactory;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements JasimaWorkStationListenerEvolveFactory {

	private static final long serialVersionUID = -9030410769000603334L;

	private NguyenR1Listener listener = null;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// Does nothing.
	}

	@Override
	public JasimaWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new NguyenR1Listener();
		}
		return listener;
	}

}
