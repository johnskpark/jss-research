package app.simConfig.hildebrandtConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -1768576838776638337L;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
