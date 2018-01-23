package app.simConfig.holthausConfig4;

import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import app.simConfig.DynamicBreakdownSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;

public class Holthaus4SimConfig extends DynamicBreakdownSimConfig {

	private static final int MIN_PROC_TIME = 1;
	private static final int MAX_PROC_TIME = 49;
	private static final int MEAN_PROC_TIME = 25;

	// Try this later down the line:
	// 90% for BL = (0%, 2.5%, 5%), 80% for BL = (10%, 15%)
	private static final double[] UTIL_LEVEL = new double[]{0.80, 0.90};
	private static final int MIN_NUM_OPS = 2;
	private static final int MAX_NUM_OPS = 10;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Double> numRTFs; // repair time factors: (1.5, 5.5, 10.5)
	private List<Double> numBLs; // breakdown levels: (0%, 2.5%, 5%, 10%, 15%)
	private List<Integer> numDDFs; // due date factors: (3, 5)
	private int numConfigs;

	private boolean hasZeroBL = false;

	public Holthaus4SimConfig(List<Double> repairTimeFactors,
			List<Double> breakdownLevels,
			List<Integer> dueDateFactors) {
		numRTFs = repairTimeFactors;
		numBLs = breakdownLevels;
		numDDFs = dueDateFactors;

		hasZeroBL = breakdownLevels.contains(0.0);
		numConfigs = repairTimeFactors.size() *
				(breakdownLevels.size() - ((hasZeroBL) ? 1 : 0)) *
				dueDateFactors.size() + ((hasZeroBL) ? dueDateFactors.size() : 0);
	}

	@Override
	public int getNumMachines(int index) {
		return NUM_MACHINES;
	}

	@Override
	public DblStream getProcTime(int index) {
		return new ProcTimeStream(MIN_PROC_TIME, MAX_PROC_TIME, getLongValueForJob());
	}

	@Override
	public double getUtilLevel(int index) {
		double bl = getBreakdownLevel(index);

		// Get the maximum utilisation rate possible that adds with breakdown level to less than 100%.
		for (int i = UTIL_LEVEL.length - 1; i >= 1; i--) {
			if (bl + UTIL_LEVEL[i] < 1.0) {
				return UTIL_LEVEL[i];
			}
		}
		return UTIL_LEVEL[0]; // Return the lowest utilisation rate.
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
	public int getNumIgnore(int index) {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs(int index) {
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public DblStream getRepairTimeDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double repairTime = getMeanRepairTime(index);

		return new DblDistribution(experiment.getMachineRandom(), new ExponentialDistribution(repairTime));
	}

	@Override
	public DblStream getTimeBetweenFailureDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double meanBreakdown = getMeanBreakdownLevel(index);

		return new DblDistribution(experiment.getMachineRandom(), new ExponentialDistribution(meanBreakdown));
	}

	@Override
	public double getBreakdownLevel(int index) {
		int blIndex;
		if (hasZeroBL) {
			if (index < numDDFs.size()) {
				blIndex = 0;
			} else {
				blIndex = (index - numDDFs.size()) / (numRTFs.size() * numDDFs.size()) + 1;
			}
		} else {
			blIndex = index / (numRTFs.size() * numDDFs.size());
		}


		return numBLs.get(blIndex);
	}

	// Repair time is dependent on the processing time, and the down time is
	// dependent on repair time and breakdown level.
	@Override
	public double getMeanRepairTime(int index) {
		if (getBreakdownLevel(index) == 0.0) {
			return 1.0;
		}

		int rtfIndex;
		if (hasZeroBL) {
			if (index < numDDFs.size()) {
				rtfIndex = 0; // just choose the first one
			} else {
				rtfIndex = ((index - numDDFs.size()) / numDDFs.size()) % numRTFs.size();
			}
		} else {
			rtfIndex = (index / numDDFs.size()) % numRTFs.size();
		}

		double repairTimeFactor = numRTFs.get(rtfIndex);
		return repairTimeFactor * MEAN_PROC_TIME;
	}

	private double getMeanBreakdownLevel(int index) {
		double breakdownLevel = getBreakdownLevel(index);
		double repairTime = getMeanRepairTime(index);

		return repairTime / breakdownLevel - repairTime;
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

}
