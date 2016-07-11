package app.simConfig.holthausConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -9063400761986620802L;

	public static final String P_INSTANCES = "instances";

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean rotatesSeed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
