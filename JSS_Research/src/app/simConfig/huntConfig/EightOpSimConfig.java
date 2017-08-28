package app.simConfig.huntConfig;

import jasima.core.random.continuous.DblStream;
import app.simConfig.DynamicSimConfig;
import app.simConfig.huntConfig.DDFStream.DDFDefinition;
import app.simConfig.huntConfig.ProcTimeStream.ProcTimeRange;

public class EightOpSimConfig extends DynamicSimConfig {

	private static final double[] UTIL_LEVEL = new double[]{0.85, 0.95};
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
	public DblStream getProcTime(int index) {
		return new ProcTimeStream(ProcTimeRange.LOW_PROC_TIME, getLongValue());
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL[index];
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		return new DDFStream(DDFDefinition.TRAIN, getLongValue());
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
	public int getNumIgnore(int index) {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs(int index) {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public int getNumConfigs() {
		return NUM_CONFIG;
	}

}
