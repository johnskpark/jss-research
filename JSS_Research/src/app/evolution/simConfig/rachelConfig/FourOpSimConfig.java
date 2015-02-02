package app.evolution.simConfig.rachelConfig;

import jasima.core.random.continuous.DblStream;
import app.SimulatorConfiguration;

public class FourOpSimConfig extends SimulatorConfiguration {

	private static final int[] MIN_OP_PROC = new int[]{1, 1, 1, 1};
	private static final int[] MAX_OP_PROC = new int[]{49, 99, 49, 99};
	private static final double[] UTIL_LEVEL = new double[]{0.90, 0.90, 0.97, 0.97};
	private static final int[] MIN_NUM_OPS = new int[]{4, 6, 8, 10, 2};
	private static final int[] MAX_NUM_OPS = new int[]{4, 6, 8, 10, 10};

	private static final int NUM_MACHINES = 10;

	private static final int NUM_SIM_PROP = 4;
	
	private static final int NUM_CONFIG = 20;

	private DblStream weightStream;
	
	@Override
	public void setSeed(long seed) {
		super.setSeed(seed);
		
		weightStream = new WeightStream(seed);
	}
	
	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}
	
	@Override
	public int getMinOpProc(int index) {
		return MIN_OP_PROC[index % NUM_SIM_PROP];
	}

	@Override
	public int getMaxOpProc(int index) {
		return MAX_OP_PROC[index % NUM_SIM_PROP];
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL[index % NUM_SIM_PROP];
	}

	@Override
	public double getDueDateFactor(int index) {
		return 0; // TODO
	}

	@Override
	public DblStream getWeight(int index) {
		return weightStream;
	}
	
	@Override
	public int getMinNumOps(int index) {
		return MIN_NUM_OPS[index / NUM_SIM_PROP];
	}

	@Override
	public int getMaxNumOps(int index) {
		return MAX_NUM_OPS[index / NUM_SIM_PROP];
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIG;
	}
	
}
