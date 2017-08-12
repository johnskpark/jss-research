package app.simConfig.holthausConfig2;

import java.util.List;

import app.simConfig.DynamicSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblStream;

/**
 * This is the Holthaus configuration without the machine breakdown used
 * to test the original implementation of Holthaus's machine breakdown
 * simulation configurations.
 *
 * @author parkjohn
 *
 */
public class Holthaus2SimConfig extends DynamicSimConfig {

	private static final int MIN_PROC_TIME = 1;
	private static final int MAX_PROC_TIME = 49;

	private static final double UTIL_LEVEL = 0.90;
	private static final int MIN_NUM_OPS = 2;
	private static final int MAX_NUM_OPS = 10;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Integer> numDDFs; // due date factors
	private int numConfigs;

	public Holthaus2SimConfig(List<Integer> dueDateFactors) {
		numDDFs = dueDateFactors;
		numConfigs = dueDateFactors.size();
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
		int ddfIndex = index % numDDFs.size();
		return new DblConst(numDDFs.get(ddfIndex));
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
	public int getNumIgnore() {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs() {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

}
