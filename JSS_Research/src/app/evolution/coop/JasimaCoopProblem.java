package app.evolution.coop;

import jasima.core.experiment.Experiment;

import java.util.ArrayList;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
// TODO need to add in niching at some point.
public class JasimaCoopProblem extends JasimaGPProblem implements GroupedProblemForm {

	private static final long serialVersionUID = -1068923215891516182L;

	public static final String P_COOP_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	private AbsGPPriorityRule coopRule;
	private IJasimaCoopFitness fitness;

	private int numSubpops;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the solver.
		coopRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_COOP_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaCoopFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaCoopFitness.class);

		// Setup the number of subpopulations.
        numSubpops = state.parameters.getInt((new Parameter(Initializer.P_POP)).push(Population.P_SIZE), null, 1);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void preprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] prepareForFitnessAssessment,
			final boolean countVictoriesOnly) {
		// Reset the seed for the simulator.
		getSimConfig().setSeed(getRandom().nextLong());

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
		// The fitness would have been cleared by then.
		for (int i = 0; i < pop.subpops.length; i++ ) {
			if (assessFitness[i]) {
				fitness.setObjectiveFitness(state, pop.subpops[i].individuals);
			}
		}

		fitness.clear();
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
		if (hasTracker()) { config.setTracker(getTracker()); }

		coopRule.setConfiguration(config);

		fitness.loadIndividuals(inds);

		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, coopRule, i);

			experiment.runExperiment();

			fitness.accumulateObjectiveFitness(inds, experiment.getResults());

			if (hasTracker()) {
				getTracker().clear();
			}
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		fitness.setTrialFitness(state, inds, updateFitness, shouldSetContext());
		fitness.setDiversityFitness(state, inds, updateFitness);

		getSimConfig().resetSeed();
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		state.output.fatal("JasimaCoopProblem must be used in a grouped problem form");
	}

	public int getNumSubpops() {
		return numSubpops;
	}

	@Override
	public Object clone() {
		JasimaCoopProblem newObject = (JasimaCoopProblem)super.clone();

		newObject.coopRule = coopRule;
		newObject.fitness = fitness;
		newObject.numSubpops = numSubpops;

		return newObject;
	}

}
