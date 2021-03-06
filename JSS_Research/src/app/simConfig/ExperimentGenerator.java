package app.simConfig;

import java.util.Random;

import app.jasimaShopSim.models.DynamicBreakdownShopExperiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.models.staticShop.StaticShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import jasima.shopSim.util.ExtendedJobStatCollector;

public class ExperimentGenerator {

	public static final JobShopExperiment getExperiment(final SimConfig simConfig,
			final PR rule,
			final int index) {
		JobShopExperiment experiment = null;

		if (simConfig instanceof DynamicBreakdownSimConfig) {
			experiment = getDynamicBreakdownShopExperiment((DynamicBreakdownSimConfig) simConfig, index);
		} else if (simConfig instanceof DynamicSimConfig) {
			experiment = getDynamicShopExperiment((DynamicSimConfig) simConfig, index);
		} else if (simConfig instanceof StaticSimConfig) {
			experiment = getStaticShopExperiment((StaticSimConfig) simConfig, index);
		} else {
			throw new IllegalArgumentException("Unrecognised SimConfig type: " + simConfig.getClass().getName());
		}

		experiment.setSequencingRule(rule);

		return experiment;
	}

	@SuppressWarnings("unchecked")
	private static DynamicBreakdownShopExperiment getDynamicBreakdownShopExperiment(
			final DynamicBreakdownSimConfig simConfig,
			final int index) {
		DynamicBreakdownShopExperiment experiment = new DynamicBreakdownShopExperiment();

		long jobValue = simConfig.getLongValueForJob();
		long machineValue = simConfig.getLongValueForMachine();

		experiment.setInitialSeed(jobValue);
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setProcTimes(simConfig.getProcTime(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs(index));
//		experiment.setStopArrivalsAfterNumJobs(simConfig.getStopAfterNumJobs());

		experiment.setMachineRandom(new Random(machineValue));
		experiment.setRepairTimeDistribution(simConfig.getRepairTimeDistribution(experiment, index));
		experiment.setTimeBetweenFailureDistribution(simConfig.getTimeBetweenFailureDistribution(experiment, index));

		BasicJobStatCollector basicStatCollector = new BasicJobStatCollector();
		ExtendedJobStatCollector extendedStatCollector = new ExtendedJobStatCollector();
		basicStatCollector.setIgnoreFirst(simConfig.getNumIgnore(index));
		extendedStatCollector.setIgnoreFirst(simConfig.getNumIgnore(index));
		experiment.setShopListener(new NotifierListener[]{basicStatCollector, extendedStatCollector});

		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@SuppressWarnings("unchecked")
	private static DynamicShopExperiment getDynamicShopExperiment(
			final DynamicSimConfig simConfig,
			final int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		long value = simConfig.getLongValue();

		experiment.setInitialSeed(value);
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setProcTimes(simConfig.getProcTime(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs(index));
//		experiment.setStopArrivalsAfterNumJobs(simConfig.getStopAfterNumJobs());

		BasicJobStatCollector basicStatCollector = new BasicJobStatCollector();
		ExtendedJobStatCollector extendedStatCollector = new ExtendedJobStatCollector();
		basicStatCollector.setIgnoreFirst(simConfig.getNumIgnore(index));
		extendedStatCollector.setIgnoreFirst(simConfig.getNumIgnore(index));
		experiment.setShopListener(new NotifierListener[]{basicStatCollector, extendedStatCollector});

		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@SuppressWarnings("unchecked")
	private static StaticShopExperiment getStaticShopExperiment(
			final StaticSimConfig simConfig,
			final int index) {
		StaticShopExperiment experiment = new StaticShopExperiment();

		experiment.setInstFileName(simConfig.getInstFileName(index));

		BasicJobStatCollector basicStatCollector = new BasicJobStatCollector();
		ExtendedJobStatCollector extendedStatCollector = new ExtendedJobStatCollector();

		experiment.setShopListener(new NotifierListener[]{basicStatCollector, extendedStatCollector});

		return experiment;
	}

}
