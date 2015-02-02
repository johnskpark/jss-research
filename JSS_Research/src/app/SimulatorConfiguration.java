package app;

public abstract class SimulatorConfiguration {

	// TODO store configuration for the simulator.

	private long seed;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long s) {
		seed = s;
	}

	public abstract int getNumMachines();
	
	public abstract double getUtilLevel();
	
	public abstract int getMinOpProc();
	
	public abstract int getMaxOpProc();

	public abstract int getMinNumOps();

	public abstract int getMaxNumOps();

}
