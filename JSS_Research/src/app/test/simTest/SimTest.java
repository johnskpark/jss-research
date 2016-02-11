package app.test.simTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import app.simConfig.AbsSimConfig;
import app.simConfig.ExperimentGenerator;
import app.simConfig.huntConfig.TrainSimConfig;
import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;
import jasima.shopSim.core.PR;
import jasima.shopSim.prioRules.basic.SPT;

public class SimTest {

	private static final double RANGE_OF_ERROR = 0.001;

	// TODO awesome, this is failing, which means that I either implemented the streams incorrectly, or that the Jasima code is fucked. 
	
	@Test
	public void consistencyTest_HuntTrain() {
		AbsSimConfig simConfig = new TrainSimConfig();
		simConfig.setSeed(17);
		PR spt = new SPT();
		
		List<Double> twtValues = new ArrayList<Double>();
		
		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			Experiment experiment = ExperimentGenerator.getExperiment(simConfig, spt, expIndex);
			experiment.runExperiment();
			
			SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");
			twtValues.add(stat.sum());
		}
		
		simConfig.resetSeed();
		
		for (int i = 0; i < 1000; i++) {
			for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
				Experiment experiment = ExperimentGenerator.getExperiment(simConfig, spt, expIndex);
				experiment.runExperiment();
				
				SummaryStat stat = (SummaryStat) experiment.getResults().get("weightedTardMean");
				
				Assert.assertEquals("The output values are not consistent with the initial run", twtValues.get(expIndex), stat.sum(), RANGE_OF_ERROR);
			}
			
			simConfig.resetSeed();
		}
	}
	
}
