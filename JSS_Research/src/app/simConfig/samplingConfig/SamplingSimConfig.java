package app.simConfig.samplingConfig;

import app.simConfig.DynamicSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblStream;

public class SamplingSimConfig extends DynamicSimConfig {

	private static final int MIN_PROC_TIME = 1;
	private static final int MAX_PROC_TIME = 49;

	// Try this later down the line:
	// 90% for BL = (0%, 2.5%, 5%), 80% for BL = (10%, 15%)
	private static final double UTIL_LEVEL = 0.90;
	private static final int MIN_NUM_OPS = 2;
	private static final int MAX_NUM_OPS = 10;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private static final int[] numDDFs = new int[] {3, 5}; // due date factors: (3, 5)
	private static final int numConfigs = 2;

	public SamplingSimConfig() {
	}

	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}

	@Override
	public DblStream getProcTime(int index) {
		return new ProcTimeStream(MIN_PROC_TIME, MAX_PROC_TIME, getLongValue());
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL;
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		return new DblConst(numDDFs[index]);
	}

	@Override
	public DblStream getWeight(int index) {
		return new WeightStream(getLongValue());
	}

	@Override
	public int getMinNumOps(int index) {
		return MIN_NUM_OPS;
	}

	@Override
	public int getMaxNumOps(int index) {
		return MAX_NUM_OPS;
	}

	@Override
	public int getNumIgnore(int index) {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs(int index) {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

}
