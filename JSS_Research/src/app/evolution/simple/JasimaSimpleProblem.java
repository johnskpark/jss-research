package app.evolution.simple;

import jasima.core.experiment.Experiment;
import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class JasimaSimpleProblem extends JasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final int NUM_INDS_IN_EVAL = 1;

	private AbsGPPriorityRule rule;
	private IJasimaSimpleFitness fitness;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the the solver.
		rule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsGPPriorityRule.class);

		// Setup the fitness.
		fitness = (IJasimaSimpleFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaSimpleFitness.class);
		setupFitness(state, base.push(P_FITNESS));
	}

	private void setupFitness(final EvolutionState state, final Parameter fitnessBase) {
		fitness.setProblem(this);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		// Reset the seed for the simulator.
		getSimConfig().setSeed(getRandom().nextLong());
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// We don't care if the individual's been evaluated previously,
		// since the simulation changes at each generation.

		long startTime = System.nanoTime();

		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
		config.setIndIndices(new int[]{0});
		config.setSubpopulations(new int[]{subpopulation});
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData)input);

		rule.setConfiguration(config);

		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, rule, i, getWorkStationListener(), getTracker());

			experiment.runExperiment();

			fitness.accumulateFitness(i, experiment.getResults());
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		fitness.setFitness(state, (JasimaGPIndividual) ind);
		fitness.clear();

		ind.evaluated = true;

		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;

		System.out.printf("%d ns\n", timeDiff);
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
