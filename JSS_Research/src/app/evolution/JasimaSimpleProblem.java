package app.evolution;

import app.SimulatorConfiguration;
import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaSimpleProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private AbsPriorityRule rule;
	private IJasimaFitness fitness;

	private SimulatorConfiguration simConfig;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver
		rule = (AbsPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsPriorityRule.class);
		fitness = (IJasimaFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaFitness.class);

		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simConfig.setSeed(state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED));
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			JasimaGPConfiguration config = new JasimaGPConfiguration();
			config.setState(state);
			config.setIndividuals(new GPIndividual[]{(GPIndividual)ind});
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);

			rule.setConfiguration(config);

			// TODO
			for (int i = 0; i < simConfig.getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, rule, i);
	
				experiment.runExperiment();
				experiment.getResults();
				
				fitness.setFitness(state, ind, experiment.getResults());
			}

			ind.evaluated = true;
		}
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(final EvolutionState state, AbsPriorityRule rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(0); // TODO
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));
		experiment.setInitialSeed(simConfig.getSeed());

		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public Object clone() {
		JasimaSimpleProblem newObject = (JasimaSimpleProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;

		return newObject;
	}

}
