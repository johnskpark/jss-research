package app.simConfig.holthausConfigRepeat;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -9063400761986620802L;

	public static final String P_INSTANCES = "instances";
	public static final String P_JOB_SEED = "job-seed";
	public static final String P_MACHINE_SEED = "machine-seed";
	public static final String P_ROTATE_SEED = "rotate-seed";

	public static final long DEFAULT_SEED = 15;

	private long initialJobSeed;
	private long initialMachineSeed;
	private MersenneTwisterFast jobRand;
	private MersenneTwisterFast machineRand;
	private boolean rotateSeed;

	private DynamicBreakdownSimConfig simConfig = null;
	private boolean initialSeedSet = false;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		String instances = state.parameters.getStringWithDefault(base.push(P_INSTANCES), null, null);
		if (instances != null) {
			simConfig = HolthausRepeatSimConfigGenerator.getSimConfig(instances);
			if (simConfig != null) {
				state.output.message("Configuration loaded for simulator: " + simConfig.getClass().getSimpleName());
			} else {
				state.output.fatal("Unrecognised configuration for the simulator. " + instances);
			}
		} else {
			state.output.fatal("No instances specified for the simulator");
		}

		initialJobSeed = state.parameters.getLongWithDefault(base.push(P_JOB_SEED), null, DEFAULT_SEED);
		initialMachineSeed = state.parameters.getLongWithDefault(base.push(P_MACHINE_SEED), null, DEFAULT_SEED);
		jobRand = new MersenneTwisterFast(initialJobSeed);
		machineRand = new MersenneTwisterFast(initialMachineSeed);

		rotateSeed = state.parameters.getBoolean(base.push(P_ROTATE_SEED), null, true);
	}

	@Override
	public boolean rotatesSeed() {
		return rotateSeed;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (!initialSeedSet) {
			simConfig.setJobSeed(initialJobSeed);
			simConfig.setMachineSeed(initialMachineSeed);
			initialSeedSet = true;
		} else if (rotateSeed) {
			simConfig.setJobSeed(jobRand.nextLong());
			simConfig.setMachineSeed(machineRand.nextLong());
		}

		return simConfig;
	}

}
