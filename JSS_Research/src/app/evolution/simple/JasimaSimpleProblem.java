package app.evolution.simple;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaSimpleProblem extends GPProblem implements IJasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private AbsGPPriorityRule rule;
	private IJasimaSimpleFitness fitness;

	private AbsSimConfig simConfig;
	private long simSeed;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the the solver.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));
		
		// Setup the fitness.
		fitness = (IJasimaSimpleFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaSimpleFitness.class);
		setupFitness(state, base.push(P_FITNESS));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	private void setupFitness(final EvolutionState state, final Parameter fitnessBase) {
		fitness.setProblem(this);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		simConfig.setSeed(simSeed);
	}

	@Override
	public void evaluate(final EvolutionState state,
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

				fitness.accumulateFitness(experiment.getResults());
			}

			fitness.setFitness(state, ind);
			fitness.clear();

			ind.evaluated = true;
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
		JasimaSimpleProblem newObject = (JasimaSimpleProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
