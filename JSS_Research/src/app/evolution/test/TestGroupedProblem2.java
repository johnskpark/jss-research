package app.evolution.test;

import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.HashMap;
import java.util.Map;

import app.evolution.AbsPriorityRule;
import app.evolution.JasimaGPConfiguration;
import app.evolution.JasimaGPData;
import app.evolution.priorityRules.BasicPriorityRule;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;

public class TestGroupedProblem2 extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final String WT_MEAN_STR = "weightedTardMean";
	public static final String PH_DIV_STR = "phenotypeDiversity";

	public static final long DEFAULT_SEED = 15;

	private AbsPriorityRule rule = new BasicPriorityRule();
	private AbsPriorityRule ensembleRule = new TestEnsembleRule();

	private AbsSimConfig simConfig;
	private long simSeed;

	private boolean ensembleEvaluated = false;
	private Map<GPIndividual, StatCollector> ensembleStats = new HashMap<GPIndividual, StatCollector>();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		simConfig.setSeed(simSeed);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		ensembleStats.clear();
		ensembleEvaluated = false;
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		long startTime = System.currentTimeMillis();

		evaluateInd(state, ind, subpopulation, threadnum);
		evaluateEnsemble(state, subpopulation, threadnum);

		long endTime = System.currentTimeMillis();
		long timeDiff = endTime - startTime;

		System.out.printf("%d\n", timeDiff);
	}

	private void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			SummaryStat stat = new SummaryStat(WT_MEAN_STR);

			JasimaGPConfiguration config = new JasimaGPConfiguration();
			config.setState(state);
			config.setIndividuals(new GPIndividual[]{(GPIndividual)ind});
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);

			rule.setConfiguration(config);

			for (int i = 0; i < simConfig.getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, rule, i);

				experiment.runExperiment();

				stat.combine((SummaryStat) experiment.getResults().get(WT_MEAN_STR));
			}

			MultiObjectiveFitness fitness = (MultiObjectiveFitness) ind.fitness;
			fitness.getObjectives()[0] = stat.mean();
			fitness.getObjectives()[1] = ind.size();

			ind.evaluated = true;
		}
	}

	private void evaluateEnsemble(final EvolutionState state,
			final int subpopulation,
			final int threadnum) {
		if (!ensembleEvaluated) {
			Individual[] inds = state.population.subpops[0].individuals;

			GPIndividual[] ensemble = new GPIndividual[inds.length];
			for (int i = 0; i < inds.length; i++) {
				GPIndividual gpInd = (GPIndividual) inds[i];
				ensemble[i] = gpInd;
				ensembleStats.put(gpInd, new StatCollector());
			}

			JasimaGPConfiguration config = new JasimaGPConfiguration();
			config.setState(state);
			config.setIndividuals(ensemble);
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);

			ensembleRule.setConfiguration(config);

			for (int i = 0; i < simConfig.getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, rule, i);

				experiment.runExperiment();

				for (int j = 0; j < ensemble.length; j++) {
					StatCollector newStat = new StatCollector();
					newStat.add(Math.sqrt(ensembleStats.get(ensemble[j]).sumSq()));

					ensembleStats.put(ensemble[j], newStat);
				}
			}

			for (int i = 0; i < inds.length; i++) {
				MultiObjectiveFitness fitness = (MultiObjectiveFitness) inds[i].fitness;
				fitness.getObjectives()[2] = ensembleStats.get(inds[i]).mean();
			}

			ensembleEvaluated = true;
		}
	}

	public Map<GPIndividual, StatCollector> getEnsembleStats() {
		return ensembleStats;
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(final EvolutionState state, AbsPriorityRule rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public Object clone() {
		TestGroupedProblem2 newObject = (TestGroupedProblem2)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
