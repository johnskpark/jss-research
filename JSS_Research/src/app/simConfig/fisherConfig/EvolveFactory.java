package app.simConfig.fisherConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.SimConfig;
import app.simConfig.StaticSimConfig;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -36593261045215937L;

	private StaticSimConfig simConfig;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// No setup required.
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (simConfig == null) {
			simConfig = new SixBySixSimConfig();
		}
		return simConfig;
	}

}
