package app.simConfig.holthausConfigRepeat;

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
 * The modification changes the due date tightness factor and the number of
 * operations per job.
 *
 * @author parkjohn
 *
 */
public class HolthausRepeatSimConfig extends DynamicBreakdownSimConfig {

	private static final int MIN_PROC_TIME = 1;
	private static final int MAX_PROC_TIME = 49;
	private static final int MEAN_PROC_TIME = 25;

	private static final double UTIL_LEVEL = 0.90;
	private static final int MIN_NUM_OPS = 2;
	private static final int MAX_NUM_OPS = 10;

	private static final int NUM_MACHINES = 10;

	private static final int NUM_IGNORE = 500;
	private static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Double> numRTFs; // repair time factors: (1, 5, 10)
	private List<Double> numBLs; // breakdown levels: (0%, 2.5%, 5%)
	private List<Integer> numDDFs; // due date factors: (3, 5)
	private int numRepeat;
	private int numScenarios;
	private int numConfigs;

	public HolthausRepeatSimConfig(List<Double> repairTimeFactors,
			List<Double> breakdownLevels,
			List<Integer> dueDateFactors,
			int repeat) {
		numRTFs = repairTimeFactors;
		numBLs = breakdownLevels;
		numDDFs = dueDateFactors;
		numRepeat = repeat;

		numScenarios = repairTimeFactors.size() *
				breakdownLevels.size() *
				dueDateFactors.size();
		numConfigs = numScenarios * numRepeat;
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
		return STOP_AFTER_NUM_JOBS;
	}

	@Override
	public DblStream getRepairTimeDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double meanRepairTime = getMeanRepairTime(index);

		return new DblDistribution(experiment.getMachineRandom(), new ExponentialDistribution(meanRepairTime));
	}

	@Override
	public DblStream getTimeBetweenFailureDistribution(DynamicBreakdownShopExperiment experiment, int index) {
		double meanBreakdown = getMeanTimeBetweenFailures(index);

		return new DblDistribution(experiment.getMachineRandom(), new ExponentialDistribution(meanBreakdown));
	}

	@Override
	public double getBreakdownLevel(int index) {
		int repeatIndex = index % numScenarios;
		int blIndex = (repeatIndex / (numDDFs.size())) % numBLs.size();

		return numBLs.get(blIndex);
	}

	// Repair time is dependent on the processing time, and the down time is
	// dependent on repair time and breakdown level.
	@Override
	public double getMeanRepairTime(int index) {
		int repeatIndex = index % numScenarios;
		int rtfIndex = repeatIndex / (numBLs.size() * numDDFs.size());
		double repairTimeFactor = numRTFs.get(rtfIndex);

		return repairTimeFactor * MEAN_PROC_TIME;
	}

	private double getMeanTimeBetweenFailures(int index) {
		double breakdownLevel = getBreakdownLevel(index);
		double meanRepairTime = getMeanRepairTime(index);

		return meanRepairTime / breakdownLevel - meanRepairTime;
	}

	@Override
	public int getNumConfigs() {
		return numConfigs;
	}

	// Added for the multitask approach.
	public int getNumScenarios() {
		return numScenarios;
	}

	public List<Integer> getNeighbourScenarios(int scenario) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public List<Integer> getIndicesForScenario(int scenario) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
