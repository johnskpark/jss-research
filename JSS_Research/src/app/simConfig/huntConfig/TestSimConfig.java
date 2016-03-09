package app.simConfig.huntConfig;

import app.simConfig.DynamicSimConfig;
import app.simConfig.huntConfig.DDFStream.DDFDefinition;
import app.simConfig.huntConfig.ProcTimeStream.ProcTimeRange;
import jasima.core.random.continuous.DblStream;

public class TestSimConfig extends DynamicSimConfig {

	private static final ProcTimeRange[] PROC_TIME = new ProcTimeRange[]{
			ProcTimeRange.LOW_PROC_TIME, 
			ProcTimeRange.HIGH_PROC_TIME,
			ProcTimeRange.LOW_PROC_TIME, 
			ProcTimeRange.HIGH_PROC_TIME
	};
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
	public DblStream getProcTime(int index) {
		return new ProcTimeStream(PROC_TIME[index % NUM_SIM_PROP], getLongValue());
	}

	@Override
	public double getUtilLevel(int index) {
		return UTIL_LEVEL[index % NUM_SIM_PROP];
	}

	@Override
	public DblStream getDueDateFactor(int index) {
		return new DDFStream(DDFDefinition.TEST, getLongValue());
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
