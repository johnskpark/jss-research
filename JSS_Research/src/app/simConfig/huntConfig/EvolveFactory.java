package app.simConfig.huntConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.DynamicSimConfig;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -6937033040584394614L;

	public static final String P_INSTANCES = "instances";
	public static final String P_SEED = "seed";
	public static final String P_ROTATE_SEED = "rotate-seed";

	public static final long DEFAULT_SEED = 15;

	private long initialSeed;
	private MersenneTwisterFast rand;
	private boolean rotateSeed;

	private DynamicSimConfig simConfig = null;
	private boolean initialSeedSet = false;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		String instances = state.parameters.getStringWithDefault(base.push(P_INSTANCES), null, null);
		if (instances != null) {
			simConfig = HuntSimConfigGenerator.getSimConfig(instances);
			if (simConfig == null) {
				state.output.fatal("Unrecognised instances for the simulator. " + instances);
			}
		} else {
			state.output.fatal("No instances specified for the simulator.");
		}

		initialSeed = state.parameters.getLongWithDefault(base.push(P_SEED), null, DEFAULT_SEED);
		rand = new MersenneTwisterFast(initialSeed);

		rotateSeed = state.parameters.getBoolean(base.push(P_ROTATE_SEED), null, true);
	}

	@Override
	public boolean rotatesSeed() {
		return rotateSeed;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (!initialSeedSet) {
			simConfig.setSeed(initialSeed);
			initialSeedSet = true;
		} else if (rotateSeed) {
			simConfig.setSeed(rand.nextLong());
		}

		return simConfig;
	}

}
