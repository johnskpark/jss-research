package app.test.simTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import app.simConfig.DynamicSimConfig;
import app.simConfig.ExperimentGenerator;
import app.simConfig.huntConfig.EightOpSimConfig;
import app.simConfig.huntConfig.FourOpSimConfig;
import app.simConfig.huntConfig.TestSimConfig;
import app.simConfig.huntConfig.TrainSimConfig;
import jasima.core.experiment.Experiment;
import jasima.core.random.continuous.DblStream;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.PR;
import jasima.shopSim.prioRules.basic.SPT;

public class SimTest {

	private static final double RANGE_OF_ERROR = 0.001;

	private static final int REPEAT = 500;

	private DynamicSimConfig simConfig;
	private PR rule;

	private List<Double> dueDates;
	private List<Double> weights;

	private List<Long> seeds;
	private List<Double> perfValues;

	@Before
	public void init() {
		dueDates = new ArrayList<Double>();
		weights = new ArrayList<Double>();

		seeds = new ArrayList<Long>();
		perfValues = new ArrayList<Double>();
	}


	@Test
	public void consistencyTest_HuntTrain4op() {
		simConfig = new FourOpSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns();

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(i);
		}
	}

	@Test
	public void consistencyTest_HuntTrain8op() {
		simConfig = new EightOpSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns();

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(i);
		}
	}

	@Test
	public void consistencyTest_HuntTrain() {
		simConfig = new TrainSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns();

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(i);
		}
	}

	@Test
	public void consistencyTest_HuntTest() {
		simConfig = new TestSimConfig();
		simConfig.setSeed(17);
		rule = new SPT();

		initialRuns();

		for (int i = 0; i < REPEAT; i++) {
			repeatRuns(i);
		}
	}

	private void initialRuns() {
		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			// Get a few samples of the due date and the weights.
			DblStream dueDate = simConfig.getDueDateFactor(expIndex);
			DblStream weight = simConfig.getWeight(expIndex);
			dueDates.add(dueDate.nextDbl());
			weights.add(weight.nextDbl());

			// Setup the experiment.
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig, rule, expIndex);
			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");

			seeds.add(experiment.getInitialSeed());
			perfValues.add(stat.sum());
		}

		simConfig.reset();
	}

	private void repeatRuns(int index) {
		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			// Get a few samples of the due date and the weights.
			DblStream dueDate = simConfig.getDueDateFactor(expIndex);
			DblStream weight = simConfig.getWeight(expIndex);

			Assert.assertEquals("The due dates are not consistent with the initial run for index " + index, dueDates.get(expIndex), new Double(dueDate.nextDbl()), RANGE_OF_ERROR);
			Assert.assertEquals("The weights are not consistent with the initial run for index " + index, weights.get(expIndex), new Double(weight.nextDbl()), RANGE_OF_ERROR);

			// Setup the experiment.
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig, rule, expIndex);
			experiment.runExperiment();

			SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");

			Assert.assertEquals("The seeds are not consistent with the initial run for index " + index, seeds.get(expIndex), new Long(experiment.getInitialSeed()));
			Assert.assertEquals("The output values are not consistent with the initial run for index " + index, perfValues.get(expIndex), stat.sum(), RANGE_OF_ERROR);
		}

		simConfig.reset();
	}

}
