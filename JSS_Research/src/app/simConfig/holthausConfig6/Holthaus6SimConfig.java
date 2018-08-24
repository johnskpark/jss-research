package app.simConfig.holthausConfig6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import app.simConfig.DynamicBreakdownSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;

// This is the config without any machine breakdowns to test the multitask approach. 
public class Holthaus6SimConfig extends DynamicBreakdownSimConfig {

	public static final double[] NUM_DUE_DATE_FACTORS = new double[]{3, 5};

	public static final int NUM_SCENARIOS = 1; // Based on the repair time factors and breakdown levels above.

	public static final int MIN_PROC_TIME = 1;
	public static final int MAX_PROC_TIME = 49;
	public static final int MEAN_PROC_TIME = 25;

	public static final double BREAKDOWN_LEVEL = 0;
	public static final double REPAIR_TIME_FACTOR = 1;
	
	// Try this later down the line:
	// 90% for BL = (0%, 2.5%, 5%), 80% for BL = (10%, 15%)
	public static final double[] UTIL_LEVEL = new double[]{0.80, 0.90};
	public static final int MIN_NUM_OPS = 2;
	public static final int MAX_NUM_OPS = 10;

	public static final int NUM_MACHINES = 10;

	public static final int NUM_IGNORE = 500;
	public static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Integer> ddfs; // due date factors: (3, 5)

	private int numDDFs;
	private int numScenarios;
	private int numConfigs;

	public Holthaus6SimConfig(List<Integer> dueDateFactors) {
		ddfs = dueDateFactors;

		numDDFs = ddfs.size();
		numScenarios = 1;
		numConfigs = numScenarios * numDDFs;
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
		int ddfIndex = index % ddfs.size();
		return new DblConst(ddfs.get(ddfIndex));
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
		return BREAKDOWN_LEVEL;
	}

	// Repair time is dependent on the processing time, and the down time is
	// dependent on repair time and breakdown level.
	@Override
	public double getMeanRepairTime(int index) {
		return REPAIR_TIME_FACTOR * MEAN_PROC_TIME;
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
	@Override
	public int getNumScenarios() {
		return NUM_SCENARIOS;
	}

	@Override
	public List<Integer> getNeighbourScenarios(int scenario) {
		return new ArrayList<>(); // Return an empty list. 
	}

	@Override
	public List<Integer> getIndicesForScenario(int scenario) {
		return Arrays.asList(new Integer[] {0, 1});
	}

}
