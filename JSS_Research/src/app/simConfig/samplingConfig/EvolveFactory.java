package app.simConfig.samplingConfig;

import app.evolution.ISimConfigEvolveFactory;
import app.simConfig.DynamicSimConfig;
import app.simConfig.SimConfig;
import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class EvolveFactory implements ISimConfigEvolveFactory {

	private static final long serialVersionUID = -9064400761986620802L;

	public static final String P_JOB_SEED = "job-seed";
	public static final String P_ROTATE_SEED = "rotate-seed";

	public static final long DEFAULT_SEED = 15;

	private long initialJobSeed;
	private MersenneTwisterFast jobRand;
	private boolean rotateSeed;

	private DynamicSimConfig simConfig = null;
	private boolean initialSeedSet = false;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		simConfig = SamplingSimConfigGenerator.getSimConfig();

		initialJobSeed = state.parameters.getLongWithDefault(base.push(P_JOB_SEED), null, DEFAULT_SEED);
		jobRand = new MersenneTwisterFast(initialJobSeed);

		rotateSeed = state.parameters.getBoolean(base.push(P_ROTATE_SEED), null, true);
	}

	@Override
	public boolean rotatesSeed() {
		return rotateSeed;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (!initialSeedSet) {
			simConfig.setSeed(initialJobSeed);
			initialSeedSet = true;
		} else if (rotateSeed) {
			simConfig.setSeed(jobRand.nextLong());
		}

		return simConfig;
	}

}
