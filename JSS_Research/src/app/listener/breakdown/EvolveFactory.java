package app.listener.breakdown;

import app.JasimaWorkStationListener;
import app.evolution.JasimaWorkStationListenerEvolveFactory;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements JasimaWorkStationListenerEvolveFactory {

	private static final long serialVersionUID = -5456898101455154800L;
	private BreakdownListener listener = null;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// No setup required.
	}

	@Override
	public JasimaWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new BreakdownListener();
		}
		return listener;
	}

}
