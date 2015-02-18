package app.evolution.coop;

import java.util.ArrayList;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.evolution.JasimaGPData;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

public class JasimaCoopProblem extends GPProblem implements GroupedProblemForm, IJasimaGPProblem {

	public static final String P_SHOULD_SET_CONTEXT = "set-context";
	private boolean shouldSetContext;

	public static final String P_IND_RULE = "rule";
	public static final String P_GROUP_RULE = "groupRule";

	public static final String P_FITNESS = "fitness";

	public static final String P_SIMULATOR = "simulator";
	public static final String P_SEED = "seed";
//	public static final String P_GROUPING = "grouping";
	public static final String P_TRACKER = "tracker";

	public static final long DEFAULT_SEED = 15;

	private AbsGPPriorityRule rule;
	private AbsGPPriorityRule groupRule;

	private IJasimaCoopFitness fitness;

	private IJasimaCoopTracker tracker = null;

	private AbsSimConfig simConfig;
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
		fitness = (IJasimaCoopFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaCoopFitness.class);

		// Setup the simulator configurations.
		simConfig = (AbsSimConfig) state.parameters.getInstanceForParameterEq(base.push(P_SIMULATOR), null, AbsSimConfig.class);
		setupSimulator(state, base.push(P_SIMULATOR));

		// Setup the tracker.
		tracker = (IJasimaCoopTracker) state.parameters.getInstanceForParameterEq(base.push(P_TRACKER), null, IJasimaCoopTracker.class);
		setupTracker(state, base.push(P_TRACKER));
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
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// TODO Auto-generated method stub

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
		newObject.rule = rule;
		newObject.groupRule = groupRule;
		newObject.fitness = fitness;
		newObject.tracker = tracker;
		newObject.simConfig = simConfig;
		newObject.simSeed = simSeed;

		return newObject;
	}

}
