package app.simConfig.mbConfig4;

import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import app.simConfig.DynamicBreakdownSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;

/**
 * This is a modification of the Holthaus configuration used in the paper
 *
 * Scheduling in job shops with machine breakdowns: an experimental study
 *
 * @author parkjohn
 *
 */
public class MB4SimConfig extends DynamicBreakdownSimConfig {

	public static final double[] NUM_REPAIR_TIME_FACTORS = new double[] {1.5, 5.5, 10.5};
	public static final double[] NUM_BREAKDOWN_LEVELS = new double[] {0.0, 0.025, 0.05, 0.1, 0.15};
	public static final double[] NUM_DUE_DATE_FACTORS = new double[] {3, 5};
	public static final double[] NUM_STOP_AFTER_NUM_JOBS = new double[] {2500, 5000, 7500};

	public static final int MIN_PROC_TIME = 1;
	public static final int MAX_PROC_TIME = 49;
	public static final int MEAN_PROC_TIME = 25;

	public static final double UTIL_LEVEL = 0.80;
	public static final int MIN_NUM_OPS = 2;
	public static final int MAX_NUM_OPS = 10;

	public static final int NUM_MACHINES = 10;
	public static final int NUM_IGNORE = 500;

	private List<Double> numRTFs; // repair time factors: (1.5, 5.5, 10.5)
	private List<Double> numBLs; // breakdown levels: (0%, 2.5%, 5%, 10%, 15%)
	private List<Integer> numDDFs; // due date factors: (3, 5)
	private List<Integer> numSANJs; // stop after num jobs: (2500, 5000, 7500)
	private int numConfigs;

	public MB4SimConfig(List<Double> repairTimeFactors,
			List<Double> breakdownLevels,
			List<Integer> dueDateFactors,
			List<Integer> stopAfterNumJobs) {
		numRTFs = repairTimeFactors;
		numBLs = breakdownLevels;
		numDDFs = dueDateFactors;
		numSANJs = stopAfterNumJobs;

		numConfigs = repairTimeFactors.size() *
				breakdownLevels.size() *
				dueDateFactors.size() *
				stopAfterNumJobs.size();
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
	public int getNumIgnore(int index) {
		return NUM_IGNORE;
	}

	@Override
	public int getStopAfterNumJobs(int index) {
		int sanjIndex = (index / (numBLs.size() * numDDFs.size() * numRTFs.size())) % numSANJs.size();

		return numSANJs.get(sanjIndex);
	}

	@Override
	public DblStream getRepairTimeDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double repairTime = getMeanRepairTime(index);

		return new DblConst(repairTime);
	}

	@Override
	public DblStream getTimeBetweenFailureDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double meanBreakdown = getMeanBreakdownLevel(index);

		return new DblDistribution(experiment.getMachineRandom(), new ExponentialDistribution(meanBreakdown));
	}

	@Override
	public double getBreakdownLevel(int index) {
		int blIndex = (index / (numDDFs.size())) % numBLs.size();

		return numBLs.get(blIndex);
	}

	// Repair time is dependent on the processing time, and the down time is
	// dependent on repair time and breakdown level.
	@Override
	public double getMeanRepairTime(int index) {
		int rtfIndex = (index / (numBLs.size() * numDDFs.size())) % numRTFs.size();
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

	// Added for the multitask approach.
	public int getNumScenarios() {
		// TODO
		return 0;
	}

	public List<Integer> getNeighbourScenarios(int scenario) {
		// TODO
		return null;
	}

	public List<Integer> getIndicesForScenario(int scenario) {
		// TODO
		return null;
	}

}
