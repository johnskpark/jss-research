package app.listener.hunt;

import app.evolution.IWorkStationListenerEvolveFactory;
import app.listener.IWorkStationListener;
import ec.EvolutionState;
import ec.util.Parameter;

public class HuntListenerEvolveFactory implements IWorkStationListenerEvolveFactory {

	private static final long serialVersionUID = -8366845139333729953L;

	public static final String P_MAX_JOBS = "max-jobs";

	private int maxSize;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		try {
			maxSize = state.parameters.getInt(base.push(P_MAX_JOBS), null);
		} catch (NumberFormatException ex) {
			state.output.fatal("Maximum number of job size not set for HuntListener.");
		}
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		return new HuntListener(maxSize);
	}

}
