package app.simConfig.holthausConfig;

import java.util.List;
import java.util.Random;

import app.simConfig.DynamicBreakdownSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblStream;
import jasima.core.random.discrete.IntUniformRange;

public class HolthausSimConfig extends DynamicBreakdownSimConfig {

	private static final int MIN_PROC_TIME = 1;
	private static final int MAX_PROC_TIME = 49;

	private static final double UTIL_LEVEL = 0.90;
	private static final int MIN_NUM_OPS = 2;
	private static final int MAX_NUM_OPS = 10;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Double> numRTFs; // repair time factors
	private List<Double> numBLs; // breakdown levels
	private List<Integer> numDDFs; // due date factors
	private int numConfigs;

	public HolthausSimConfig(List<Double> repairTimeFactors,
			List<Double> breakdownLevels,
			List<Integer> ddfs) {
		numRTFs = repairTimeFactors;
		numBLs = breakdownLevels;
		numDDFs = ddfs;
		numConfigs = repairTimeFactors.size() * breakdownLevels.size() * ddfs.size();
	}

	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}

	@Override
	public DblStream getProcTime(int index) {
		return new IntUniformRange(new Random(getLongValueForJob()), MIN_PROC_TIME, MAX_PROC_TIME);
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
		return new WeightStream(getLongValueForJob());
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
	public double getRepairTimeFactor(int index) {
		int rtfIndex = index / (numBLs.size() * numDDFs.size());
		return numRTFs.get(rtfIndex);
	}

	@Override
	public double getBreakdownLevel(int index) {
		int blIndex = (index / (numDDFs.size())) % numRTFs.size();
		return numBLs.get(blIndex);
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

}
