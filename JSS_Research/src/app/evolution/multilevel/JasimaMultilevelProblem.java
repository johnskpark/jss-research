package app.evolution.multilevel;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.ArrayList;
import java.util.Random;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.listener.hunt.HuntListener;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaMultilevelProblem extends GPProblem implements MLSProblemForm, IJasimaGPProblem {

	private static final long serialVersionUID = -5150181943760622786L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";

	public static final String P_GROUP_RULE = "group-rule";
	public static final String P_GROUP_FITNESS = "group-fitness";

	public static final String P_IND_RULE = "ind-rule";
	public static final String P_IND_FITNESS = "ind-fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";
	public static final String P_TRACKER = "tracker";

	public static final long DEFAULT_SEED = 15;

	private boolean shouldSetContext;

	private AbsGPPriorityRule groupRule;
	private IJasimaMultilevelGroupFitness groupFitness;

	private AbsGPPriorityRule indRule;
	private IJasimaMultilevelIndividualFitness indFitness;

	private AbsSimConfig simConfig;
	private long simSeed;
	private Random rand;

	private IJasimaTracker tracker;

	private int numSubpops;

	private HuntListener huntListener = new HuntListener(5);  // FIXME Incorporate into the parameter.

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Load whether we should set context or not.
		shouldSetContext = state.parameters.getBoolean(base.push(P_SHOULD_SET_CONTEXT), null, true);

		// Setup the GPData.
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the individual solver for evaluating groups.
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, AbsGPPriorityRule.class);
		groupFitness = (IJasimaMultilevelGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_FITNESS), null, IJasimaMultilevelGroupFitness.class);

		// Setup the individual solver for evaluating individuals.
		indRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, AbsGPPriorityRule.class);
		indFitness = (IJasimaMultilevelIndividualFitness) state.parameters.getInstanceForParameterEq(base.push(P_IND_FITNESS), null, IJasimaMultilevelIndividualFitness.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		simSeed = state.parameters.getLongWithDefault(base.push(P_SIMULATOR).push(P_SEED), null, DEFAULT_SEED);
		rand = new Random(simSeed);

		// Setup the tracker.
		tracker = (IJasimaTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaTracker.class);
		tracker.setProblem(this);

		// Setup the number of subpopulations.
        numSubpops = state.parameters.getInt((new Parameter(Initializer.P_POP)).push(Population.P_SIZE), null, 1);

		// Feed in the shop simulation listener to input.
        ((JasimaGPData) input).setWorkStationListener(huntListener);
	}

	@Override
	public void preprocessPopulation(EvolutionState state,
			Population pop,
			boolean[] prepareForFitnessAssessment,
			boolean countVictoriesOnly) {
		// Reset the seed for the simulator.
		simConfig.setSeed(rand.nextLong());

		for (int i = 0; i < pop.subpops.length; i++) {
			if (!prepareForFitnessAssessment[i]) {
				continue;
			}

			for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
				Fitness fitness = pop.subpops[i].individuals[j].fitness;
				fitness.trials = new ArrayList();
			}
		}
	}

	@Override
	public void postprocessPopulation(EvolutionState state,
			Population pop,
			boolean[] assessFitness,
			boolean countVictoriesOnly) {
		// TODO Auto-generated method stub. Will this be same as the GroupedProblemForm?

	}

	@Override
	public void evaluateSubpop(final EvolutionState state,
			final MLSSubpopulation subpop,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		GPIndividual[] gpInds = new GPIndividual[subpop.individuals.length];
		for (int i = 0; i < subpop.individuals.length; i++) {
			gpInds[i] = (GPIndividual) subpop.individuals[i];
		}

		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(gpInds);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);
		config.setTracker(tracker);

		groupRule.setConfiguration(config);
		groupFitness.loadIndividuals(gpInds);

		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, groupRule, expIndex);

			experiment.runExperiment();

			groupFitness.accumulateFitness(expIndex, gpInds, experiment.getResults(), tracker);
			tracker.clear();
		}

		groupFitness.setFitness(state, subpop, updateFitness, shouldSetContext);
		groupFitness.clear();

		simConfig.resetSeed();
	}

	@Override
	public void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
		config.setSubpopulations(new int[]{subpopulation});
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);
		config.setTracker(tracker);

		indRule.setConfiguration(config);

		for (int expIndex = 0; expIndex < simConfig.getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, indRule, expIndex);

			experiment.runExperiment();

			indFitness.accumulateFitness(expIndex, experiment.getResults());
		}

		indFitness.setFitness(state, ind);
		indFitness.clear();

		simConfig.resetSeed();
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
		experiment.addMachineListener(huntListener);
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		state.output.warning("evaluateInd should be called instead of evaluate for JasimaMultilevelProblem.");

		evaluateInd(state, ind, subpopulation, threadnum);
	}

	@Override
	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	@Override
	public int getNumInds() {
		return numSubpops;
	}

}
