package app.simConfig.holthausConfig5;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import app.simConfig.DynamicBreakdownSimConfig;
import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;

public class Holthaus5SimConfig extends DynamicBreakdownSimConfig {

	public static final double[] NUM_REPAIR_TIME_FACTORS = new double[]{1, 5, 10};
	public static final double[] NUM_BREAKDOWN_LEVELS = new double[]{0.0, 0.025, 0.05};
	public static final double[] NUM_DUE_DATE_FACTORS = new double[]{3, 5};

	public static final int NUM_SCENARIOS = 7; // Based on the repair time factors and breakdown levels above.

	public static final int MIN_PROC_TIME = 1;
	public static final int MAX_PROC_TIME = 49;
	public static final int MEAN_PROC_TIME = 25;

	// Try this later down the line:
	// 90% for BL = (0%, 2.5%, 5%), 80% for BL = (10%, 15%)
	public static final double[] UTIL_LEVEL = new double[]{0.80, 0.90};
	public static final int MIN_NUM_OPS = 2;
	public static final int MAX_NUM_OPS = 10;

	public static final int NUM_MACHINES = 10;

	public static final int DUE_DATE_FACTOR = 4;

	public static final int NUM_IGNORE = 500;
	public static final int STOP_AFTER_NUM_JOBS = 2500;

	private List<Double> rtfs; // repair time factors: (1, 5, 10)
	private List<Double> bls; // breakdown levels: (0%, 2.5%, 5%)

	private int numRTFs;
	private int numBLs;
	private int numScenarios;
	private int numConfigs;

	private boolean hasZeroBL = false;

	private List<Node> nodes;

	public Holthaus5SimConfig(List<Double> repairTimeFactors, List<Double> breakdownLevels) {
		rtfs = repairTimeFactors;
		bls = breakdownLevels;

		hasZeroBL = breakdownLevels.contains(0.0);

		numRTFs = rtfs.size();
		numBLs = bls.size();
		numScenarios = numRTFs * (numBLs - ((hasZeroBL) ? 1 : 0)) + ((hasZeroBL) ? 1 : 0);
		numConfigs = numScenarios;

		initNeighbourRelations();
	}

	private void initNeighbourRelations() {
		nodes = new ArrayList<Node>(NUM_SCENARIOS);

		int[] r = new int[NUM_SCENARIOS];
		int[] b = new int[NUM_SCENARIOS];

		// Add the nodes.
		for (int i = 0; i < NUM_SCENARIOS; i++) {
			r[i] = (i == 0) ? 0 : (i - 1) % NUM_REPAIR_TIME_FACTORS.length;
			b[i] = (i == 0) ? 0 : (i - 1) / NUM_REPAIR_TIME_FACTORS.length + 1;

			Node node1 = new Node(i);
			for (int j = 0; j < NUM_DUE_DATE_FACTORS.length; j++) {
				node1.indices.add(i * NUM_DUE_DATE_FACTORS.length + j);
			}

			nodes.add(node1);

			// Add the neighbourhood relations.
			if (b[i] > 0) {
				int j = (NUM_BREAKDOWN_LEVELS[b[i]-1] == 0) ? 0 : i - NUM_REPAIR_TIME_FACTORS.length;

				Node node2 = nodes.get(j);

				node1.neighbours.add(node2);
				node2.neighbours.add(node1);
			}
		}
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
		return new DblConst(DUE_DATE_FACTOR);
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
			if (index == 0) {
				blIndex = 0;
			} else {
				blIndex = (index - 1) / rtfs.size() + 1;
			}
		} else {
			blIndex = index / rtfs.size();
		}

		return bls.get(blIndex);
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
			if (index == 0) {
				rtfIndex = 0;
			} else {
				rtfIndex = (index - 1) % rtfs.size();
			}
		} else {
			rtfIndex = index % rtfs.size();
		}

		double repairTimeFactor = rtfs.get(rtfIndex);
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
		return NUM_SCENARIOS;
	}

	public List<Integer> getNeighbourScenarios(int scenario) {
		return nodes.get(scenario).neighbours.stream().map(x -> x.index).collect(Collectors.toList());
	}

	public List<Integer> getIndicesForScenario(int scenario) {
		return nodes.get(scenario).indices;
	}

	private class Node {
		List<Node> neighbours = new ArrayList<Node>();
		List<Integer> indices = new ArrayList<Integer>();
		int index;

		public Node(int index) {
			this.index = index;
		}
	}

}
