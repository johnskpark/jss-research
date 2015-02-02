package app;

import jasima.core.random.continuous.DblStream;

public abstract class SimulatorConfiguration {

	// TODO store configuration for the simulator.

	private long seed;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long s) {
		seed = s;
	}
	
	public abstract int getNumMachines(int index);
	
	public abstract int getMinOpProc(int index);
	
	public abstract int getMaxOpProc(int index);

	public abstract double getUtilLevel(int index);
	
	public abstract DblStream getDueDateFactor(int index);
	
	public abstract DblStream getWeight(int index);
	
	public abstract int getMinNumOps(int index);

	public abstract int getMaxNumOps(int index);

	public abstract int getNumConfigs();

}
