package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;
import app.simConfig.DynamicSimConfig;

public class TestSimConfig extends DynamicSimConfig {

	private static final int[] MIN_OP_PROC = new int[]{1, 1, 1, 1};
	private static final int[] MAX_OP_PROC = new int[]{49, 99, 49, 99};
	private static final double[] UTIL_LEVEL = new double[]{0.90, 0.90, 0.97, 0.97};
	private static final int[] MIN_NUM_OPS = new int[]{4, 6, 8, 10, 2};
	private static final int[] MAX_NUM_OPS = new int[]{4, 6, 8, 10, 10};

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private static final int NUM_SIM_PROP = 4;

	private static final int NUM_CONFIG = 20;

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
	public DblStream getDueDateFactor(int index) {
		return new TestDDFStream(getLongValue());
	}

	@Override
	public DblStream getWeight(int index) {
		return new WeightStream(getLongValue());
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
	public int getNumIgnore() {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs() {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIG;
	}

}
