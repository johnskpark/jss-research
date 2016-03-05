package app.simConfig.taillardConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.SimConfig;
import app.simConfig.StaticSimConfig;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = 2060007581402901073L;

	private StaticSimConfig simConfig;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// No setup required.
	}

	@Override
	public SimConfig generateSimConfig() {
		if (simConfig == null) {
			simConfig = new TaillardSimConfig();
		}
		return simConfig;
	}

}
