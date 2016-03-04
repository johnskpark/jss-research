package app.simConfig;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.models.staticShop.StaticShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

public class ExperimentGenerator {

	public static final JobShopExperiment getExperiment(final SimConfig simConfig, final PR rule, final int index) {
		JobShopExperiment experiment = null;

		if (simConfig instanceof DynamicSimConfig) {
			experiment = getDynamicShopExperiment((DynamicSimConfig) simConfig, rule, index);
		} else if (simConfig instanceof StaticSimConfig) {
			experiment = getStaticShopExperiment((StaticSimConfig) simConfig, rule, index);
		} else {
			throw new IllegalArgumentException("Unrecognised SimConfig type: " + simConfig.getClass().getName());
		}

		experiment.setSequencingRule(rule);

		return experiment;
	}

	@SuppressWarnings("unchecked")
	private static DynamicShopExperiment getDynamicShopExperiment(final DynamicSimConfig simConfig, final PR rule, final int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs());

		BasicJobStatCollector statCollector = new BasicJobStatCollector();
		statCollector.setIgnoreFirst(simConfig.getNumIgnore());
		experiment.setShopListener(new NotifierListener[]{statCollector});

		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	private static StaticShopExperiment getStaticShopExperiment(final StaticSimConfig simConfig, final PR rule, final int index) {
		StaticShopExperiment experiment = new StaticShopExperiment();

		experiment.setInstFileName(simConfig.getInstFileName(index));

		return experiment;
	}

}
