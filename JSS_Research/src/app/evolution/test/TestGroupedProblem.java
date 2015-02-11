package app.evolution.test;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaFitness;
import app.evolution.IJasimaGrouping;
import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.fitness.MOTWTFitness;
import app.evolution.grouping.PopGrouping;
import app.evolution.priorityRules.BasicPriorityRule;
import app.evolution.priorityRules.EnsemblePriorityRule;
import app.evolution.tracker.DecisionTracker;
import app.simConfig.AbsSimConfig;
import app.simConfig.rachelConfig.FourOpSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class TestGroupedProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final String P_GROUPING = "grouping";

	public static final String P_TRACKER = "tracker";

	public static final long DEFAULT_SEED = 15;

	private AbsGPPriorityRule indRule = new BasicPriorityRule();
	private AbsGPPriorityRule ensembleRule = new EnsemblePriorityRule();
	private IJasimaFitness fitness = new MOTWTFitness();

	private IJasimaGrouping grouping = new PopGrouping();
	private IJasimaTracker tracker = new DecisionTracker();

	private AbsSimConfig simConfig = new FourOpSimConfig();
	private long simSeed = DEFAULT_SEED;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		grouping.setup(state, base.push(P_GROUPING));
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		simConfig.setSeed(simSeed);

		// Set the new grouping scheme.
		grouping.clearForGeneration(state);
		grouping.groupIndividuals(state, threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		fitness.clearTrackerFitness();
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (ind.evaluated) {
			System.out.println("This... Complicates things.");
		}

		if (!ind.evaluated) {
			long startTime = System.currentTimeMillis();

			// Evaluate the individual separately.
			if (!grouping.isIndEvaluated()) {
				evaluateInd(state, ind, subpopulation, threadnum);
			}

			// Evaluate the grouping that the individual's part of.
			if (!grouping.isGroupEvaluated()) {
				evaluateGroup(state, grouping.getGroups(ind), subpopulation, threadnum);
			}

			fitness.setFitness(state, ind);
			fitness.clearFitness();

			ind.evaluated = true;

			long endTime = System.currentTimeMillis();
			long timeDiff = endTime - startTime;

			System.out.printf("%d\n", timeDiff);
		}
	}

	protected void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
		config.setSubpopulations(new int[]{subpopulation});
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData)input);

		indRule.setConfiguration(config);

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, indRule, i);

			experiment.runExperiment();

			fitness.accumulateFitness(experiment.getResults());
		}
	}

	protected void evaluateGroup(final EvolutionState state,
			final GPIndividual[][] groups,
			final int subpopulation,
			final int threadnum) {
		for (int i = 0; i < groups.length; i++) {
			GPIndividual[] group = groups[i];

			JasimaGPConfig config = new JasimaGPConfig();
			config.setState(state);
			config.setIndividuals(group);
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);
			config.setTracker(tracker);

			ensembleRule.setConfiguration(config);

			for (int j = 0; j < simConfig.getNumConfigs(); j++) {
				Experiment experiment = getExperiment(state, ensembleRule, j);

				experiment.runExperiment();

				fitness.accumulateTrackerFitness(tracker.getResults());
				tracker.clear();
			}
		}

		grouping.setGroupEvaluated(true);
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(final EvolutionState state, AbsGPPriorityRule rule, int index) {
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
		TestGroupedProblem newObject = (TestGroupedProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.indRule = indRule;
		newObject.ensembleRule = ensembleRule;
		newObject.fitness = fitness;
		newObject.grouping = grouping;
		newObject.tracker = tracker;

		return newObject;
	}

}
