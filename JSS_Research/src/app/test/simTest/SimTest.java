package app.test.simTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.DynamicSimConfig;
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import app.simConfig.holthausConfig.HolthausSimConfig;
import app.simConfig.huntConfig.EightOpSimConfig;
import app.simConfig.huntConfig.FourOpSimConfig;
import app.simConfig.huntConfig.TestSimConfig;
import app.simConfig.huntConfig.TrainSimConfig;
import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.PR;
import jasima.shopSim.prioRules.basic.SPT;

public class SimTest {

	private static final double RANGE_OF_ERROR = 0.001;

	private static final String TARDINESS = "tardiness";
	private static final int REPEAT = 500;

	private PR rule;

	private List<Long> seeds;
	private List<Double> perfValues;

	@Before
	public void init() {
		seeds = new ArrayList<Long>();
		perfValues = new ArrayList<Double>();
	}


	@Test
	public void consistencyTest_HuntTrain4op() {
		DynamicSimConfig simConfig = new FourOpSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	@Test
	public void consistencyTest_HuntTrain8op() {
		DynamicSimConfig simConfig = new EightOpSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	@Test
	public void consistencyTest_HuntTrain() {
		DynamicSimConfig simConfig = new TrainSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	@Test
	public void consistencyTest_HuntTest() {
		DynamicSimConfig simConfig = new TestSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	@Test
	public void consistencyTest_HolthausAll() {
		List<Double> repairTimeFactors = Arrays.asList(new Double[]{1.0, 5.0, 10.0});
		List<Double> breakdownLevels = Arrays.asList(new Double[]{0.0, 0.025, 0.05});
		List<Integer> dueDateFactors = Arrays.asList(new Integer[]{4, 8});

		DynamicBreakdownSimConfig simConfig = new HolthausSimConfig(repairTimeFactors, breakdownLevels, dueDateFactors);
		simConfig.setJobSeed(15);
		simConfig.setMachineSeed(15);

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	@Test
	public void consistencyTest_HolthausZeroBreakdownLevel() {
		List<Double> repairTimeFactors = Arrays.asList(new Double[]{1.0, 5.0, 10.0});
		List<Double> breakdownLevels = Arrays.asList(new Double[]{0.0});
		List<Integer> dueDateFactors = Arrays.asList(new Integer[]{4, 8});

		DynamicBreakdownSimConfig simConfig = new HolthausSimConfig(repairTimeFactors, breakdownLevels, dueDateFactors);
		simConfig.setJobSeed(15);
		simConfig.setMachineSeed(15);

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}
	@Test
	public void consistencyTest_HolthausMediumBreakdownLevel() {
		List<Double> repairTimeFactors = Arrays.asList(new Double[]{1.0, 5.0, 10.0});
		List<Double> breakdownLevels = Arrays.asList(new Double[]{0.025});
		List<Integer> dueDateFactors = Arrays.asList(new Integer[]{4, 8});

		DynamicBreakdownSimConfig simConfig = new HolthausSimConfig(repairTimeFactors, breakdownLevels, dueDateFactors);
		simConfig.setJobSeed(15);
		simConfig.setMachineSeed(15);

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}
	@Test
	public void consistencyTest_HolthausHighBreakdownLevel() {
		List<Double> repairTimeFactors = Arrays.asList(new Double[]{1.0, 5.0, 10.0});
		List<Double> breakdownLevels = Arrays.asList(new Double[]{0.05});
		List<Integer> dueDateFactors = Arrays.asList(new Integer[]{4, 8});

		DynamicBreakdownSimConfig simConfig = new HolthausSimConfig(repairTimeFactors, breakdownLevels, dueDateFactors);
		simConfig.setJobSeed(15);
		simConfig.setMachineSeed(15);

		initialRuns(simConfig);

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(simConfig, i);
		}
	}

	private void initialRuns(SimConfig simConfig) {
		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			// Setup the experiment.
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig, rule, expIndex);
			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get(TARDINESS);

			seeds.add(experiment.getInitialSeed());
			perfValues.add(stat.mean());
		}

		simConfig.reset();
	}

	private void repeatRuns(SimConfig simConfig, int index) {
		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			// Setup the experiment.
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig, rule, expIndex);
			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get(TARDINESS);

			Assert.assertEquals("The seeds are not consistent with the initial run for index " + index, seeds.get(expIndex), new Long(experiment.getInitialSeed()));
			Assert.assertEquals("The output values are not consistent with the initial run for index " + index, perfValues.get(expIndex), stat.mean(), RANGE_OF_ERROR);
		}

		simConfig.reset();
	}

}
