package app.evolution.coop;

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
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
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
		setupTracker(state, base.push(P_TRACKER));

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));
	}

	private void setupSimulator(final EvolutionState state, final Parameter simBase) {
		simSeed = state.parameters.getLongWithDefault(simBase.push(P_SEED), null, DEFAULT_SEED);
	}

	private void setupTracker(final EvolutionState state, final Parameter trackerBase) {
		tracker.setProblem(this);
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
					KozaFitness fitness = (KozaFitness) pop.subpops[i].individuals[j].fitness;
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
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					KozaFitness fitness = (KozaFitness) pop.subpops[i].individuals[j].fitness;

					// we take the minimum over the trials
					double min = Double.POSITIVE_INFINITY;
					for (int l = 0; l < fitness.trials.size(); l++) {
						double trialVal = (Double) fitness.trials.get(l);
						min = Math.min(trialVal, min);  // it'll be the first one, but whatever
					}

					fitness.setStandardizedFitness(state, min);
					pop.subpops[i].individuals[j].evaluated = true;
				}
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

		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, coopRule, i);

			experiment.runExperiment();

			fitness.accumulateObjectiveFitness(inds, experiment.getResults());
			fitness.accumulateDiversityFitness(tracker.getResults());
			tracker.clear();
		}

		// TODO need to adapt this to use the new fitness measure.
		for (int i = 0; i < inds.length; i++) {
//			double trial = fitness.getFitness(stats, i);
			double trial = Double.POSITIVE_INFINITY;

			if (updateFitness[i]) {
				GPIndividual gpInd = gpInds[i];

				int len = gpInd.fitness.trials.size();

				// TODO I don't really get this part. What is it used for exactly?
				if (len == 0) {
					if (shouldSetContext) {
						gpInd.fitness.setContext(inds, i);
					}

					gpInd.fitness.trials.add(new Double(trial));
				} else if ((Double) gpInd.fitness.trials.get(0) < trial) {
					if (shouldSetContext) {
						gpInd.fitness.setContext(inds, i);
					}

					Object t = gpInd.fitness.trials.get(0);
					gpInd.fitness.trials.set(0, fitness);
					gpInd.fitness.trials.add(t);
				}

				((KozaFitness)gpInd.fitness).setStandardizedFitness(state, trial);
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
