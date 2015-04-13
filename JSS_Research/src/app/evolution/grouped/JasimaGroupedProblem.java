package app.evolution.grouped;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.Random;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaGroupedProblem extends GPProblem implements IJasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_IND_RULE = "rule";
	public static final String P_GROUP_RULE = "groupRule";

	public static final String P_FITNESS = "fitness";
	public static final String P_GROUPING = "grouping";
	public static final String P_TRACKER = "tracker";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private AbsGPPriorityRule rule;
	private AbsGPPriorityRule groupRule;

	private IJasimaGroupFitness fitness;
	private IJasimaGrouping grouping;
	private IJasimaTracker tracker;

	private AbsSimConfig simConfig;
	private Random rand;
	private long simSeed;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the solver.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, AbsGPPriorityRule.class);
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaGroupFitness.class);

		// Setup the grouping.
		grouping = (IJasimaGrouping) state.parameters.getInstanceForParameterEq(base.push(P_GROUPING), null, IJasimaGrouping.class);
		grouping.setup(state, base.push(P_GROUPING));

		// Setup the tracker.
		tracker = (IJasimaTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaTracker.class);
		setupTracker(state, base.push(P_TRACKER));

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
		rand = new Random(simSeed);
	}

	private void setupTracker(final EvolutionState state, final Parameter trackerBase) {
		tracker.setProblem(this);
	}

	public IJasimaGrouping getIndGrouping() {
		return grouping;
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		// Reset the seed for the simulator.
		simConfig.setSeed(rand.nextLong());

		// Set the new grouping scheme.
		grouping.clearForGeneration(state);
		grouping.groupIndividuals(state, threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		evaluateInd(state, ind, subpopulation, threadnum);
		evaluateGroup(state, grouping.getGroups(ind), subpopulation, threadnum);
	}

	protected void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
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

				fitness.accumulateIndFitness(ind, experiment.getResults());
			}

			fitness.setIndFitness(state, ind);
			fitness.clearIndFitness();

			ind.evaluated = true;

			simConfig.resetSeed();
		}
	}

	protected void evaluateGroup(final EvolutionState state,
			final GroupedIndividual[] group,
			final int subpopulation,
			final int threadnum) {
		for (int i = 0; i < group.length; i++) {
			if (!group[i].isEvaluated()) {
				JasimaGPConfig config = new JasimaGPConfig();
				config.setState(state);
				config.setIndividuals(group[0].getInds());
				config.setSubpopulations(new int[]{subpopulation});
				config.setThreadnum(threadnum);
				config.setData((JasimaGPData) input);
				config.setTracker(tracker);

				groupRule.setConfiguration(config);

				for (int j = 0; j < simConfig.getNumConfigs(); j++) {
					Experiment experiment = getExperiment(state, groupRule, j);

					experiment.runExperiment();

					fitness.accumulateGroupFitness(tracker.getResults());
					tracker.clear();
				}

				// TODO need to update the best ensemble of the population.

				fitness.setGroupFitness(state, group[i].getInds());
				fitness.clearGroupFitness();

				group[i].setEvaluated(true);

				simConfig.resetSeed();
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
	public int getNumInds() {
		return grouping.getGroupSize();
	}

	@Override
	public Object clone() {
		JasimaGroupedProblem newObject = (JasimaGroupedProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.groupRule = groupRule;
		newObject.fitness = fitness;
		newObject.grouping = grouping;
		newObject.tracker = tracker;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
