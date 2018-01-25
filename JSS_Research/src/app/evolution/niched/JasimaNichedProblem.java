package app.evolution.niched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import app.evolution.GPPriorityRuleBase;
import app.evolution.ISimConfigEvolveFactory;
import app.evolution.JasimaGPIndividual;
import app.evolution.niched.fitness.NicheFitnessBase;
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
import ec.Subpopulation;
import ec.gp.koza.KozaFitness;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.core.util.Pair;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

public class JasimaNichedProblem extends JasimaSimpleProblem {

	private static final long serialVersionUID = -3573529649173003108L;

	public static final String P_NICHE = "niched";
	public static final String P_NICHE_RADIUS = "niched-radius";
	public static final String P_NICHE_CAPACITY = "niched-capacity";

	public static final String P_SAMPLING = "sampling";
	public static final String P_SAMPLING_METHOD = "factory";
	public static final String P_SAMPLING_RULE = "rule";
	public static final String P_SAMPLING_SEED = "seed";

	private static final int NOT_SET = -1;

	private int numNiches = NOT_SET;
	private double nicheRadius;
	private double nicheCapacity;

	private ISimConfigEvolveFactory[] nicheSimConfigFactories;
	private SimConfig[] nicheSimConfigs;

	private SamplerFactory samplingFactory;
	private SamplingPR<Individual> samplingPR;
	private PR samplingRule;
	private int samplingSeed; // Find out whether Yi rotates the seed used for the rules or not.

	private GPPriorityRuleBase rankRule = new EvolveWATC(); // Keep it simple for now.

	private ISimConfigEvolveFactory samplingSimConfigFactory;
	private SimConfig samplingSimConfig;

	private JasimaNichedIndividual[] curGenNichedInds;
	private double[] curGenNichedIndsFitness;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		if (!(getFitness() instanceof NicheFitnessBase)) {
			state.output.fatal("The fitness must be of type NicheFitness.");
		}

		// Initialise the niches.
		NicheFitnessBase nicheFitness = (NicheFitnessBase) getFitness();
		numNiches = nicheFitness.getNumNiches(getSimConfig());

		nicheSimConfigFactories = new ISimConfigEvolveFactory[numNiches];
		nicheSimConfigs = new SimConfig[numNiches];

		for (int i = 0; i < numNiches; i++) {
			Parameter nicheParam = base.push(P_NICHE).push(i+"");

			// Setup the simulator configurations.
			// It will look something like "eval.problem.niche.0.simulator = ..."
			nicheSimConfigFactories[i] = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(nicheParam.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
			nicheSimConfigFactories[i].setup(state, nicheParam.push(P_SIMULATOR));
			nicheSimConfigs[i] = nicheSimConfigFactories[i].generateSimConfig();
		}

		nicheRadius = state.parameters.getDouble(base.push(P_NICHE_RADIUS), null);
		nicheCapacity = state.parameters.getDouble(base.push(P_NICHE_CAPACITY), null);

		// Initialise the sampler.
		try {
			Parameter samplingParam = base.push(P_SAMPLING);

			samplingFactory = (SamplerFactory) state.parameters.getInstanceForParameter(samplingParam.push(P_SAMPLING_METHOD), null, Object.class);
			samplingRule = (PR) state.parameters.getInstanceForParameter(samplingParam.push(P_SAMPLING_RULE), null, PR.class);
			samplingSeed = state.parameters.getInt(samplingParam.push(P_SAMPLING_SEED), null);

			samplingPR = samplingFactory.generateSampler(samplingRule, samplingSeed, getTracker());

			samplingSimConfigFactory = (ISimConfigEvolveFactory) state.parameters.getInstanceForParameterEq(samplingParam.push(P_SIMULATOR), null, ISimConfigEvolveFactory.class);
			samplingSimConfigFactory.setup(state, samplingParam.push(P_SIMULATOR));
			samplingSimConfig = samplingSimConfigFactory.generateSimConfig();

			samplingPR.setPriorityRules(Arrays.asList(rankRule, getRule()));
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

		curGenNichedInds = new JasimaNichedIndividual[numNiches];
		curGenNichedIndsFitness = new double[numNiches];
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		if (state.generation == 0) {
			state.population.archive = new JasimaNichedIndividual[numNiches];
		}

		Arrays.fill(curGenNichedInds, null);
		Arrays.fill(curGenNichedIndsFitness, Double.POSITIVE_INFINITY);

		NicheFitnessBase nicheFitness = (NicheFitnessBase) getFitness();
		nicheFitness.init(state, getSimConfig(), threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);

		NicheFitnessBase nicheFitness = (NicheFitnessBase) getFitness();

		// Update the nicheFitnesses of the current generation niched individuals.
		for (int i = 0; i < numNiches; i++) {
			evaluateNiched(state, curGenNichedInds[i], i, threadnum);
		}

		// Update the overall archive of overall niched individuals.
		nicheFitness.updateArchive(state, curGenNichedInds, threadnum);

		// Update the fitnesses of the individuals using the niched individuals.
		updateFitnesses(state, threadnum);
	}


	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			runStandardExperiment(state, ind, subpopulation, threadnum);

			if (hasSamplingPR()) {
				JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) ind;

				runSamplerRecording(state, nichedInd, subpopulation, threadnum);
				runSamplerTracked(state, nichedInd, subpopulation, threadnum);
			}

			ind.evaluated = true;
		}
	}

	protected void runStandardExperiment(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		configureRule(state, getRule(), null, new Individual[] {ind}, new int[] {subpopulation}, threadnum);

		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state,
					getRule(),
					i,
					getSimConfig(),
					getWorkStationListeners(),
					null);
			experiment.runExperiment();

			getFitness().accumulateFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());
//			if (hasTracker()) { getTracker().clearCurrentExperiment(); }
		}

		getFitness().setFitness(state, getSimConfig(), (JasimaGPIndividual) ind);

		clearForRun(getFitness());
	}

	// Do an initial run that samples for decision situations.
	protected void runSamplerRecording(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		configureRule(state, getRule(), null, new Individual[] {ind}, new int[] {subpopulation}, threadnum);

		for (int i = 0; i < samplingSimConfig.getNumConfigs(); i++) {
			samplingPR.initRecordingRun(samplingSimConfig, i);

			Experiment experiment = getExperiment(state,
					samplingPR,
					i,
					samplingSimConfig,
					getWorkStationListeners(),
					null);
			experiment.runExperiment();
//			if (hasTracker()) { getTracker().clearCurrentExperiment(); }
		}

		samplingSimConfig.reset();
	}

	// Rerun the sampling rule again with the individuals as part of the sampling rule.
	protected void runSamplerTracked(final EvolutionState state,
			final JasimaNichedIndividual ind,
			final int subpopulation,
			final int threadnum) {
		configureRule(state, getRule(), getTracker(), new Individual[] {ind}, new int[] {subpopulation}, threadnum);
		configureRule(state, rankRule, getTracker(), new Individual[] {ind}, new int[] {subpopulation}, threadnum);
		initialiseTracker(getTracker());

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
			if (hasTracker()) { getTracker().clearCurrentExperiment(); }
		}

		samplingSimConfig.reset();

		clearForRun(getTracker(), getSamplingPR());
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
			final Individual ind,
			final int nicheIndex,
			final int threadnum) {
		if (!(ind instanceof JasimaNichedIndividual)) {
			state.output.fatal("The niche individual must be of type JasimaNichedIndividual: " + ind.getClass());
		}

		JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) ind;
		NicheFitnessBase fitness = (NicheFitnessBase) getFitness();

		configureRule(state,
				getRule(),
				getTracker(),
				new Individual[] {ind},
				new int[] {0},
				threadnum);

		for (int i = 0; i < nicheSimConfigs[nicheIndex].getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state,
					getRule(),
					i,
					nicheSimConfigs[nicheIndex],
					getWorkStationListeners(),
					null);
			experiment.runExperiment();

			fitness.accumulateFitness(i, nicheSimConfigs[nicheIndex], nichedInd, experiment.getResults());

			clearForExperiment(getWorkStationListeners());
		}

		fitness.setNichedFitness(state, nicheIndex, nicheSimConfigs[nicheIndex], nichedInd);
		fitness.clear();

		clearForRun(fitness);
	}

	public void updateFitnesses(final EvolutionState state,
			final int threadnum) {
		// Apply the clearing algorithm here.
		for (int i = 0; i < state.population.subpops.length; i++) {
			Subpopulation subpop = state.population.subpops[i];

			List<JasimaNichedIndividual> sortedInds = Arrays.stream(subpop.individuals)
					.map(x -> (JasimaNichedIndividual) x)
					.collect(Collectors.toList());

			Collections.sort(sortedInds, new Comparator<JasimaNichedIndividual>() {
				@Override
				public int compare(JasimaNichedIndividual ind1, JasimaNichedIndividual ind2) {
					if (ind1.getFitness().betterThan(ind2.getFitness())) {
						return -1;
					} else if (ind2.getFitness().betterThan(ind1.getFitness())) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			int[] nicheCapacities = new int[numNiches];
			Arrays.fill(nicheCapacities, 1); // The niche individual counts towards the niches.

			for (int j = 1; j < sortedInds.size(); j++) { // Ignore the best individual
				JasimaNichedIndividual ind = sortedInds.get(j);

				List<Pair<Integer, Double>> nicheIndexDistancePairs = new ArrayList<>();

				boolean isNichedInd = false;

				// Calculate the distances to the niches, and add to the closest niche possible.
				// If full, then add to the second closest, then third, etc.
				for (int k = 0; k < state.population.archive.length; k++) {
					JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) state.population.archive[k];

					if (nichedInd.equals(ind)) {
						isNichedInd = true;
					}

					double distance = calculateDistance(ind, nichedInd);
					nicheIndexDistancePairs.add(new Pair<Integer, Double>(k, distance));
				}

				// If the individual is a niched individual, then just move onto the next individual.
				if (isNichedInd) {
					continue;
				}

				Collections.sort(nicheIndexDistancePairs, new Comparator<Pair<Integer, Double>>() {
					@Override
					public int compare(Pair<Integer, Double> pair1, Pair<Integer, Double> pair2) {
						if (pair1.b < pair2.b) {
							return -1;
						} else if (pair1.b > pair2.b) {
							return 1;
						} else {
							return 0;
						}
					}
				});

				boolean inOvercrowdedNiche = false;
				for (int k = 0; k < nicheIndexDistancePairs.size(); k++) {
					Pair<Integer, Double> nicheIndexDistance = nicheIndexDistancePairs.get(i);

					if (nicheIndexDistance.b > nicheRadius) {
						break;
					} else {
						if (atMaxCapacity(nicheCapacities, nicheIndexDistance.a)) {
							inOvercrowdedNiche = true;
						} else {
							inOvercrowdedNiche = false;
							nicheCapacities[nicheIndexDistance.a]++;
						}
					}
				}

				if (inOvercrowdedNiche) {
					// Assign the worst fitness to the individual.
					((KozaFitness) ind.getFitness()).setStandardizedFitness(state, Double.MAX_VALUE);
				}
			}
		}
	}

	protected boolean atMaxCapacity(int[] capacities, int index) {
		return capacities[index] >= nicheCapacity;
	}

	protected double calculateDistance(JasimaNichedIndividual ind1, JasimaNichedIndividual ind2) {
		int[] decisions1 = ind1.getRuleDecisionVector();
		int[] decisions2 = ind2.getRuleDecisionVector();

		if (decisions1.length != decisions2.length) {
			throw new RuntimeException(String.format("The decision vectors are not the same length: %d, %d", decisions1.length, decisions2.length));
		}

		double distance = 0.0;
		for (int i = 0; i < decisions1.length; i++) {
			distance += (decisions1[i] - decisions2[i]) * (decisions1[i] - decisions2[i]);
		}

		return Math.sqrt(distance);
	}

	public boolean hasSamplingPR() {
		return samplingPR != null;
	}

	public SamplingPR<Individual> getSamplingPR() {
		return samplingPR;
	}

	public JasimaNichedIndividual[] getCurGenNichedInds() {
		return curGenNichedInds;
	}

	public double[] getCurGenNichedIndsFitness() {
		return curGenNichedIndsFitness;
	}

	public int getNumNiches() {
		return numNiches;
	}

	@Override
	protected void rotateSimSeed() {
		super.rotateSimSeed();

		for (int i = 0; i < numNiches; i++) {
			nicheSimConfigs[i] = nicheSimConfigFactories[i].generateSimConfig();
		}
		samplingSimConfig = samplingSimConfigFactory.generateSimConfig();
	}

	@Override
	protected void resetSimSeed() {
		super.resetSimSeed();

		for (int i = 0; i < numNiches; i++) {
			nicheSimConfigs[i].reset();
		}
		samplingSimConfig.reset();
	}

	@Override
	public Object clone() {
		JasimaNichedProblem newObject = (JasimaNichedProblem) super.clone();

		newObject.numNiches = this.numNiches;
		newObject.nicheRadius = this.nicheRadius;
		newObject.nicheCapacity = this.nicheCapacity;
		newObject.nicheSimConfigFactories = this.nicheSimConfigFactories;
		newObject.nicheSimConfigs = this.nicheSimConfigs;

		newObject.samplingFactory = this.samplingFactory;
		newObject.samplingPR = this.samplingPR;
		newObject.samplingRule = this.samplingRule;
		newObject.samplingSeed = this.samplingSeed;

		newObject.samplingSimConfigFactory = this.samplingSimConfigFactory;
		newObject.samplingSimConfig = this.samplingSimConfig;

		newObject.rankRule = this.rankRule;

		newObject.curGenNichedInds = this.curGenNichedInds;
		newObject.curGenNichedIndsFitness = this.curGenNichedIndsFitness;

		return newObject;
	}

}
