package app.simConfig;

import jasima.core.random.continuous.DblStream;

import java.util.Random;

public abstract class DynamicSimConfig implements SimConfig {

	private long seed;

	private Random rand;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long s) {
		seed = s;
		rand = new Random(seed);
	}

	public long getLongValue() {
		return rand.nextLong();
	}

	public void reset() {
		setSeed(seed);
	}

	public abstract DblStream getProcTime(int index);

	public abstract double getUtilLevel(int index);

	public abstract DblStream getDueDateFactor(int index);

	public abstract DblStream getWeight(int index);

	public abstract int getMinNumOps(int index);

	public abstract int getMaxNumOps(int index);

	public abstract int getNumIgnore(int index);

	// FIXME need to add in the option to stop after nth job completion instead as well.
	public abstract int getStopAfterNumJobs(int index);

}
