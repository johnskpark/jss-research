package app.evolution.ensemble.eurogp;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.ArrayList;
import java.util.Random;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class EuroGPProblem extends GPProblem implements GroupedProblemForm, IJasimaGPProblem {

	private static final long serialVersionUID = -1068923215891516182L;

	public static final String P_SHOULD_SET_CONTEXT = "set-context";
	public static final String P_COOP_RULE = "rule";
	public static final String P_FITNESS = "fitness";
	public static final String P_TRACKER = "tracker";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";

	public static final long DEFAULT_SEED = 15;

	private boolean shouldSetContext;

	private EuroGPPR rule;
	private EuroGPFitness fitness;
	private EuroGPTracker tracker;

	private AbsSimConfig simConfig;
	private Random rand;
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

		rule = new EuroGPPR();
		fitness = new EuroGPFitness();
		tracker = new EuroGPTracker();

		tracker.setProblem(this);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
		rand = new Random(simSeed);
	}

	/************************************************/

	@Override
	public void preprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] prepareForFitnessAssessment,
			final boolean countVictoriesOnly) {
		// Reset the seed for the simulator.
		simConfig.setSeed(rand.nextLong());

		numSubpops = pop.subpops.length;
		for (int i = 0; i < pop.subpops.length; i++) {
			if (prepareForFitnessAssessment[i]) {
				preprocessSubpopulation(state, pop.subpops[i]);
			}
		}
	}

	private void preprocessSubpopulation(final EvolutionState state,
			Subpopulation subpop) {
		for (int j = 0; j < subpop.individuals.length; j++) {
			subpop.individuals[j].fitness.trials = new ArrayList();
		}
	}

	/************************************************/

	@Override
	public void postprocessPopulation(final EvolutionState state,
			Population pop,
			final boolean[] assessFitness,
			final boolean countVictoriesOnly) {
		for (int i = 0; i < pop.subpops.length; i++ ) {
			if (assessFitness[i]) {
				postprocessSubpopulation(state, pop.subpops[i]);
			}
		}
	}

	private void postprocessSubpopulation(final EvolutionState state,
			Subpopulation subpop) {
		for (int j = 0; j < subpop.individuals.length; j++ ) {
			KozaFitness fit = (KozaFitness) subpop.individuals[j].fitness;

			// we take the max over the trials
			double min = getMinimumOverTrials(fit.trials);

			fit.setStandardizedFitness(state, min);
			subpop.individuals[j].evaluated = true;
		}
	}

	private double getMinimumOverTrials(ArrayList trials) {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < trials.size(); i++) {
			min = Math.min((Double) trials.get(i), min);
		}
		return min;
	}

	/************************************************/

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

		rule.setConfiguration(config);

		fitness.loadIndividuals(gpInds);

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, rule, i);

			experiment.runExperiment();

			fitness.accumulateFitness(gpInds, experiment, tracker);
			tracker.clear();
		}

		fitness.setFitness(state, inds, updateFitness, shouldSetContext);
		fitness.clear();

		simConfig.resetSeed();
	}

	// FIXME This should be part of the simulation config later down the line.

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

	/************************************************/

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
		EuroGPProblem newObject = (EuroGPProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;

		newObject.tracker = tracker;
		newObject.tracker.setProblem(newObject);

		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
