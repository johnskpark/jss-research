package app.simConfig.taillardConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.SimConfig;
import app.simConfig.StaticSimConfig;
import ec.EvolutionState;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = 2060007581402901073L;

	public static final String P_INSTANCES = "instances";
	
	private StaticSimConfig simConfig;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		String instances = state.parameters.getStringWithDefault(base.push(P_INSTANCES), null, null);
		if (instances != null) {
			simConfig = TaillardSimConfigGenerator.getSimConfig(instances);
			if (simConfig != null) {
				state.output.message("Configuration loaded for simulator: " + simConfig.getClass().getSimpleName());
			} else {
				state.output.fatal("Unrecognised configuration for the simulator. " + instances);
			}
		} else {
			state.output.fatal("No instances specified for the simulator.");
		}
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		return simConfig;
	}

}
