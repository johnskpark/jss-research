package app.evolution.simple;

import app.evolution.GPPriorityRuleBase;
import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

public class JasimaSimpleProblem extends JasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final int NUM_INDS_IN_EVAL = 1;

	private GPPriorityRuleBase rule;
	private IJasimaFitness<JasimaGPIndividual> fitness;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the the solver.
		rule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, GPPriorityRuleBase.class);
		getWorkStationListeners().add(rule);

		// Setup the fitness.
		fitness = (IJasimaFitness<JasimaGPIndividual>) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaFitness.class);
		fitness.setProblem(this);

		// Setup the tracker.
		if (hasTracker()) { getTracker().addRule(rule); }
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum, rule);

		for (Subpopulation subpop : state.population.subpops) {
			for (Individual ind : subpop.individuals) {
				ind.evaluated = false;
			}
		}
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum, rule);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			configureRule(state,
					rule,
					getTracker(),
					new Individual[] {ind},
					new int[] {subpopulation},
					threadnum);
			initialiseTracker(getTracker());

			for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, rule, i, getWorkStationListeners(), getTracker());
				experiment.runExperiment();

				fitness.accumulateFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

				clearForExperiment(getWorkStationListeners());
			}

			fitness.setFitness(state, getSimConfig(), (JasimaGPIndividual) ind);
			fitness.clear();

			ind.evaluated = true;

			clearForRun(getTracker());
		}
	}

	public GPPriorityRuleBase getRule() {
		return rule;
	}

	public IJasimaFitness<JasimaGPIndividual> getFitness() {
		return fitness;
	}

	@Override
	public Object clone() {
		JasimaSimpleProblem newObject = (JasimaSimpleProblem) super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;

		return newObject;
	}

}
