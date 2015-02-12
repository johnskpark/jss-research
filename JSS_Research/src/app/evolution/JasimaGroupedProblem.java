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
	private AbsGPPriorityRule groupRule;

	private IJasimaGroupFitness fitness;

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

		// Setup the solver.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaGroupFitness.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));

		// Setup the grouping.
		grouping = (IJasimaGrouping) state.parameters.getInstanceForParameterEq(base.push(P_GROUPING), null, IJasimaGrouping.class);
		grouping.setup(state, base.push(P_GROUPING));

		// Setup the tracker.
		tracker = (IJasimaTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaTracker.class);
		setupTracker(state, base.push(P_TRACKER));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	private void setupTracker(final EvolutionState state, final Parameter trackerBase) {
		tracker.setProblem(this);
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
		}
	}

	protected void evaluateGroup(final EvolutionState state,
			final GroupedIndividual group,
			final int subpopulation,
			final int threadnum) {
		if (!group.isEvaluated()) {
			JasimaGPConfig config = new JasimaGPConfig();
			config.setState(state);
			config.setIndividuals(group.getInds());
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);
			config.setTracker(tracker);

			groupRule.setConfiguration(config);

			for (int j = 0; j < simConfig.getNumConfigs(); j++) {
				Experiment experiment = getExperiment(state, rule, j);

				experiment.runExperiment();

				fitness.accumulateGroupFitness(tracker.getResults());
				tracker.clear();
			}

			fitness.setGroupFitness(state, group.getInds());
			fitness.clearGroupFitness();

			group.setEvaluated(true);
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
		newObject.groupRule = groupRule;
		newObject.fitness = fitness;
		newObject.grouping = grouping;
		newObject.tracker = tracker;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
