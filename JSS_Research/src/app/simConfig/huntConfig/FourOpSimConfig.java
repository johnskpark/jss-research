package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;
import app.simConfig.AbsSimConfig;

public class FourOpSimConfig extends AbsSimConfig {

	private static final double[] UTIL_LEVEL = new double[]{0.85, 0.95};
	private static final int MIN_OP_PROC = 1;
	private static final int MAX_OP_PROC = 49;
	private static final int NUM_OPS = 8;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private static final int NUM_CONFIG = 2;

	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}

	@Override
	public int getMinOpProc(int index) {
		return MIN_OP_PROC;
	}

	@Override
	public int getMaxOpProc(int index) {
		return MAX_OP_PROC;
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL[index];
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		return new TrainDDFStream(getLongValue());
	}

	@Override
	public DblStream getWeight(int index) {
		return new WeightStream(getLongValue());
	}

	@Override
	public int getMinNumOps(int index) {
		return NUM_OPS;
	}

	@Override
	public int getMaxNumOps(int index) {
		return NUM_OPS;
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
