package app.evolution;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaGroupedProblem extends GPProblem implements IJasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final String P_GROUPING = "grouping";

	public static final String P_TRACKER = "tracker";

	public static final long DEFAULT_SEED = 15;

	private AbsGPPriorityRule rule;
	private IJasimaFitness fitness;

	private IJasimaGrouping grouping = null;
	private IJasimaTracker tracker = null;

	private AbsSimConfig simConfig;
	private long simSeed;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver. TODO need a way of using multiple rules for the single approach.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);
		fitness = (IJasimaFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaFitness.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));

		// Setup the grouping.
		grouping = (IJasimaGrouping) state.parameters.getInstanceForParameterEq(base.push(P_GROUPING), null, IJasimaGrouping.class);
		grouping.setup(state, base.push(P_GROUPING));

		// Setup the tracker.
		tracker = (IJasimaTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaTracker.class);
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
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
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// TODO so, what do I want here?

		// I want to be able to evaluate the ensemble as a whole, and get penalty from it.
		// I want to be able to evaluate the individual, and get fitness from it.

		// So I need to have the following:
		// evaluation for individual, and some fitness component to it.
		// evaluation for group, and some fitness component to it.

		// Okay, so how do I want to do this fitness part here?
		// So when the fitness is accumulated for each evaluation component, I need to
		// get the relevant statistics class into the fitness measure, but what's a
		// clean way of doing it?

		// Ah fuck it. For now, let's just do tracker fitness for group, regular fitness for
		// individual.

		if (!ind.evaluated) {
			// Evaluate the individual separately.
			if (grouping.isIndEvaluated()) {
				evaluateInd(state, ind, subpopulation, threadnum);
			}

			// Evaluate the grouping that the individual's part of.
			if (grouping.isGroupEvaluated()) {
				evaluateGroup(state, grouping.getGroups(ind), subpopulation, threadnum);
			}

			fitness.setFitness(state, ind);
			fitness.clearFitness();

			ind.evaluated = true;
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

		rule.setConfiguration(config);

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, rule, i);

			experiment.runExperiment();

			fitness.accumulateFitness(experiment.getResults());
			tracker.clear();
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

			for (int j = 0; j < simConfig.getNumConfigs(); j++) {
				Experiment experiment = getExperiment(state, rule, j);

				experiment.runExperiment();

				fitness.accumulateTrackerFitness(tracker.getResults());
				tracker.clear();
			}
		}
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
	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	@Override
	public Object clone() {
		JasimaGroupedProblem newObject = (JasimaGroupedProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;
		newObject.grouping = grouping;
		newObject.tracker = tracker;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
