package app.evolution.simple;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

public class JasimaSimpleProblem extends JasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final int NUM_INDS_IN_EVAL = 1;

	private AbsGPPriorityRule rule;
	private IJasimaFitness<JasimaGPIndividual> fitness;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the the solver.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaFitness<JasimaGPIndividual>) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaFitness.class);
		fitness.setProblem(this);

		// Setup the tracker.
		if (hasTracker()) { getTracker().setPriorityRule(rule); }
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		rotateSimSeed();

		// Setup the tracker.
		if (hasTracker()) {
			getTracker().setPriorityRule(rule);
			getTracker().setSimConfig(getSimConfig());
		}

		// Evaluate the reference rule, if there is one.
		if (hasReferenceRule()) {
			clearReference();
			evaluateReference();
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// We don't care if the individual's been evaluated previously,
		// since the simulation changes at each generation.
		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
		config.setIndIndices(new int[]{0});
		config.setSubpopulations(new int[]{subpopulation});
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData)input);
		if (hasTracker()) { config.setTracker(getTracker()); }

		rule.setConfiguration(config);

		if (hasTracker()) { getTracker().initialise(); }

		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, rule, i, getWorkStationListener(), getTracker());

			experiment.runExperiment();

			fitness.accumulateFitness(i, (JasimaGPIndividual) ind, experiment.getResults());
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		fitness.setFitness(state, (JasimaGPIndividual) ind);
		fitness.clear();

		if (hasTracker()) {
			getTracker().clear();
		}

		ind.evaluated = true;

		resetSimSeed();
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
