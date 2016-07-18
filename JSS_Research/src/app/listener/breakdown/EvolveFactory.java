package app.listener.breakdown;

import app.IWorkStationListener;
import app.evolution.IWorkStationListenerEvolveFactory;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements IWorkStationListenerEvolveFactory {

	private static final long serialVersionUID = -5456898101455154800L;
	private BreakdownListener listener = null;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// No setup required.
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new BreakdownListener();
		}
		return listener;
	}

}
