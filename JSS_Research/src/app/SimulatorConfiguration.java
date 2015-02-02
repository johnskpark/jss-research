package app;

public class SimulatorConfiguration {

	// TODO store configuration for the simulator.

	private double meanProcTime;
	private int numOps;

	private long seed;

	public double getMeanProcTime() {
		return meanProcTime;
	}

	public void setMeanProcTime(double m) {
		meanProcTime = m;
	}

	public int getMinNumOps() {
		return numOps;
	}

	public void setMinNumOps(int n) {
		numOps = n;
	}

	public int getMaxNumOps() {
		return numOps;
	}

	public void setMaxNumOps(int n) {
		numOps = n;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long s) {
		seed = s;
	}

}
