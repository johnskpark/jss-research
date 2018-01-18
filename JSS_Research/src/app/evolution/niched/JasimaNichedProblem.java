package app.evolution.niched;

import java.util.List;

import app.evolution.GPPriorityRuleBase;
import app.evolution.ISimConfigEvolveFactory;
import app.evolution.JasimaGPIndividual;
import app.evolution.niched.fitness.NicheFitness;
import app.evolution.priorityRules.EvolveWATC;
import app.evolution.simple.JasimaSimpleProblem;
import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaExperimentTracker;
import app.tracker.sampler.SamplerFactory;
import app.tracker.sampler.SamplingPR;
import ec.EvolutionState;
import ec.Individual;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaNichedProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -3573529649173003108L;

	public static final String P_NICHE = "niche";

	public static final String P_SAMPLING = "sampling";
	public static final String P_SAMPLING_METHOD = "factory";
	public static final String P_SAMPLING_RULE = "rule";
	public static final String P_SAMPLING_SEED = "seed";

	private static final int NOT_SET = -1;

	private int numNiches = NOT_SET;

	private ISimConfigEvolveFactory[] nicheSimConfigFactories;
	private SimConfig[] nicheSimConfigs;

	private SamplerFactory samplingFactory;
	private SamplingPR samplingPR;
	private PR samplingRule;
	private int samplingSeed; // Find out whether Yi rotates the seed used for the rules or not.

	private GPPriorityRuleBase rankRule = new EvolveWATC(); // Keep it simple for now.

	private ISimConfigEvolveFactory samplingSimConfigFactory;
	private SimConfig samplingSimConfig;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (!(getFitness() instanceof NicheFitness)) {
			state.output.fatal("The fitness must be of type NicheFitness.");
		}

		// Initialise the niches.
		NicheFitness nicheFitness = (NicheFitness) getFitness();
		numNiches = nicheFitness.getNumNiches(getSimConfig());

		state.population.archive = new JasimaNichedIndividual[numNiches];

		nicheSimConfigs = new SimConfig[numNiches];

		for (int i = 0; i < numNiches; i++) {
			Parameter nicheParam = base.push(P_NICHE).push(i+"");

			// Setup the simulator configurations.
			// It will look something like "eval.problem.niche.0.simulator = ..."
			nicheSimConfigFactories[i] = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(nicheParam.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
			nicheSimConfigFactories[i].setup(state, nicheParam.push(P_SIMULATOR));
			nicheSimConfigs[i] = nicheSimConfigFactories[i].generateSimConfig();
		}

		// Initialise the sampler.
		try {
			Parameter samplingParam = base.push(P_SAMPLING);

			samplingFactory = (SamplerFactory) state.parameters.getInstanceForParameter(samplingParam.push(P_SAMPLING_METHOD), null, SamplerFactory.class);
			samplingRule = (PR) state.parameters.getInstanceForParameter(samplingParam.push(P_SAMPLING_RULE), null, PR.class);
			samplingSeed = state.parameters.getInt(samplingParam.push(P_SAMPLING_SEED), null);

			samplingPR = samplingFactory.generateSampler(samplingRule, samplingSeed, getTracker());

			samplingSimConfigFactory = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(samplingParam.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
			samplingSimConfigFactory.setup(state, samplingParam.push(P_SIMULATOR));
			samplingSimConfig = samplingSimConfigFactory.generateSimConfig();
		} catch (ParamClassLoadException ex) {
			state.output.warning("No sampling rule provided for JasimaGPProblem: " + ex.getMessage());

			// Reset everything.
			samplingFactory = null;
			samplingRule = null;
			samplingSeed = NOT_SET;
			samplingPR = null;
			samplingSimConfigFactory = null;
			samplingSimConfig = null;
		}

		if (hasTracker()) {
			getTracker().addRule(rankRule);
		}
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		nicheFitness.init(state, getSimConfig(), threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);

		NicheFitness nicheFitness = (NicheFitness) getFitness();
		JasimaGPIndividual[] nichedInds = nicheFitness.getNichedIndividuals();

		// Update the nicheFitnesses of the current generation niched individuals.
		for (int i = 0; i < nichedInds.length; i++) {
			evaluateNiched(state, nichedInds[i], i, threadnum);
		}

		// Update the overall archive of overall niched individuals.
		nicheFitness.updateArchive(state, getSimConfig(), threadnum);

		// Update the fitnesses of the individuals using the niched individuals.
		updateFitnesses(state, threadnum);
	}


	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			configureRule(state,
					getRule(),
					getTracker(),
					new Individual[] {ind},
					new int[] {subpopulation},
					threadnum);
			initialiseTracker(getTracker());

			runStandardExperiment(state, ind, subpopulation, threadnum);

			if (hasSamplingPR()) {
				JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) ind;

				runSamplerRecording(state, nichedInd, subpopulation, threadnum);
				runSamplerTracked(state, nichedInd, subpopulation, threadnum);
			}

			getFitness().setFitness(state, getSimConfig(), (JasimaGPIndividual) ind);
			getFitness().clear();

			ind.evaluated = true;

			clearForRun(getTracker(), getSamplingPR());
		}
	}

	protected void runStandardExperiment(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, getRule(), i, getSimConfig(), getWorkStationListeners(), getTracker());
			experiment.runExperiment();

			getFitness().accumulateFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());
		}
	}

	// Do an initial run that samples for decision situations.
	protected void runSamplerRecording(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		for (int i = 0; i < samplingSimConfig.getNumConfigs(); i++) {
			samplingPR.initRecordingRun(samplingSimConfig, i);

			Experiment experiment = getExperiment(state,
					samplingPR,
					i,
					samplingSimConfig,
					getWorkStationListeners(),
					getTracker());
			experiment.runExperiment();
		}

		samplingSimConfig.reset();
	}

	// Rerun the sampling rule again with the individuals as part of the sampling rule.
	protected void runSamplerTracked(final EvolutionState state,
			final JasimaNichedIndividual ind,
			final int subpopulation,
			final int threadnum) {
		for (int i = 0; i < samplingSimConfig.getNumConfigs(); i++) {
			samplingPR.initTrackedRun(samplingSimConfig, i);

			Experiment experiment = getExperiment(state,
					samplingPR,
					i,
					samplingSimConfig,
					getWorkStationListeners(),
					getTracker());
			experiment.runExperiment();

			calculateDiversity(state, i, experiment, ind, subpopulation, threadnum);
			getTracker().clearCurrentExperiment();
		}

		samplingSimConfig.reset();
		getTracker().clear();
	}

	// Calculate the diversity from the samples gathered from the tracked run.
	protected void calculateDiversity(final EvolutionState state,
			final int configIndex,
			final Experiment experiment,
			final JasimaNichedIndividual ind,
			final int subpopulation,
			final int threadnum) {
		JasimaExperimentTracker<Individual> tracker = getTracker();
		List<JasimaExperiment<Individual>> trackedResults = tracker.getResults();

		JasimaExperiment<Individual> trackedResult = trackedResults.get(configIndex);
		List<JasimaDecision<Individual>> decisions = trackedResult.getDecisions();

		int[] ruleDecisionVector = new int[decisions.size()];

		for (int i = 0; i < decisions.size(); i++) {
			JasimaDecision<Individual> decision = decisions.get(i);
			PrioRuleTarget selectedJob = decision.getSelectedEntry(rankRule);
			List<PrioRuleTarget> jobRankings = decision.getEntryRankings(getRule());

			for (int j = 0; j < jobRankings.size(); j++) {
				if (jobRankings.get(j).equals(selectedJob)) {
					ruleDecisionVector[i] = j;
				}
			}
		}

		ind.setRuleDecisionVector(ruleDecisionVector);
	}

	public void evaluateNiched(final EvolutionState state,
			final JasimaGPIndividual ind,
			final int nicheIndex,
			final int threadnum) {
		if (!(ind instanceof JasimaNichedIndividual)) {
			state.output.fatal("The niche individual must be of type JasimaNichedIndividual");
		}

		NicheFitness fitness = (NicheFitness) getFitness();

		configureRule(state,
				getRule(),
				getTracker(),
				new Individual[] {ind},
				new int[] {0},
				threadnum);
		initialiseTracker(getTracker());

		for (int i = 0; i < nicheSimConfigs[nicheIndex].getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, getRule(), i, getSimConfig(), getWorkStationListeners(), getTracker());
			experiment.runExperiment();

			fitness.accumulateFitness(i, nicheSimConfigs[nicheIndex], (JasimaGPIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());
		}

		fitness.setNichedFitness(state, nicheSimConfigs[nicheIndex], (JasimaNichedIndividual) ind);
		fitness.clear();

		clearForRun(getTracker());
	}

	public void updateFitnesses(final EvolutionState state,
			final int threadnum) {
		// TODO
	}

	public boolean hasSamplingPR() {
		return samplingPR != null;
	}

	public SamplingPR getSamplingPR() {
		return samplingPR;
	}

	@Override
	public Object clone() {
		JasimaNichedProblem newObject = (JasimaNichedProblem) super.clone();

		return newObject;
	}

}
