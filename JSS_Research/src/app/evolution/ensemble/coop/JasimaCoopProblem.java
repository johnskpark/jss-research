package app.evolution.ensemble.coop;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.ArrayList;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaCoopProblem extends GPProblem implements GroupedProblemForm, IJasimaGPProblem {

	private static final long serialVersionUID = -1068923215891516182L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";
	public static final String P_COOP_RULE = "rule";
	public static final String P_FITNESS = "fitness";
	public static final String P_TRACKER = "tracker";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private boolean shouldSetContext;

	private AbsGPPriorityRule coopRule;
	private IJasimaCoopFitness fitness;
	private IJasimaCoopTracker tracker;

	private AbsSimConfig simConfig;
	private long simSeed;
	private int numSubpops;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the solver.
		coopRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_COOP_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaCoopFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaCoopFitness.class);

		// Setup the tracker.
		tracker = (IJasimaCoopTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaCoopTracker.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);

		// Setup the number of subpopulations.
        numSubpops = state.parameters.getInt((new Parameter(Initializer.P_POP)).push(Population.P_SIZE), null, 1);

        setupFitness(state, base.push(P_FITNESS));
		setupTracker(state, base.push(P_TRACKER));
		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupFitness(final EvolutionState state, final Parameter fitnessBase) {
		fitness.setTracker(tracker);
	}

	private void setupTracker(final EvolutionState state, final Parameter trackerBase) {
		tracker.setProblem(this);
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void preprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] prepareForFitnessAssessment,
			final boolean countVictoriesOnly) {
		// Reset the seed for the simulator.
		simConfig.setSeed(simSeed);

		for (int i = 0; i < pop.subpops.length; i++) {
			if (prepareForFitnessAssessment[i]) {
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					Fitness fitness = pop.subpops[i].individuals[j].fitness;
					fitness.trials = new ArrayList();
				}
			}
		}
	}

	@Override
	public void postprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] assessFitness,
			final boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++ ) {
			if (assessFitness[i]) {
				fitness.setObjectiveFitness(state, pop.subpops[i].individuals);
			}
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		GPIndividual[] gpInds = new GPIndividual[inds.length];
		for (int i = 0; i < inds.length; i++) {
			gpInds[i] = (GPIndividual) inds[i];
		}

		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(gpInds);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);
		config.setTracker(tracker);

		coopRule.setConfiguration(config);

		fitness.loadIndividuals(inds);

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, coopRule, i);

			experiment.runExperiment();

			fitness.accumulateObjectiveFitness(inds, experiment.getResults());
			fitness.accumulateDiversityFitness(tracker.getResults());
			tracker.clear();
		}

		fitness.setTrialFitness(state, inds, updateFitness, shouldSetContext);
		fitness.setDiversityFitness(state, inds, updateFitness);
		fitness.clear();
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
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		state.output.fatal("JasimaCoopProblem must be used in a grouped problem form");
	}

	@Override
	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	@Override
	public int getNumInds() {
		return numSubpops;
	}

	@Override
	public Object clone() {
		JasimaCoopProblem newObject = (JasimaCoopProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.coopRule = coopRule;
		newObject.fitness = fitness;
		newObject.tracker = tracker;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}