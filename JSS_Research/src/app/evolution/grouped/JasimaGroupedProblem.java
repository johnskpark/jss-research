package app.evolution.grouped;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

public class JasimaGroupedProblem extends JasimaGPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_IND_RULE = "rule";
	public static final String P_GROUP_RULE = "groupRule";

	public static final String P_FITNESS = "fitness";
	public static final String P_GROUPING = "grouping";

	private GPPriorityRuleBase rule;
	private GPPriorityRuleBase groupRule;

	private JasimaGroupFitness fitness;
	private IJasimaGrouping grouping;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the solvers.
		if (state.parameters.exists(base.push(P_IND_RULE), null)) {
			rule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, GPPriorityRuleBase.class);
		}

		if (state.parameters.exists(base.push(P_GROUP_RULE), null)) {
			groupRule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, GPPriorityRuleBase.class);
		}

		// Setup the fitness.
		fitness = (JasimaGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, JasimaGroupFitness.class);

		// Setup the grouping.
		grouping = (IJasimaGrouping) state.parameters.getInstanceForParameterEq(base.push(P_GROUPING), null, IJasimaGrouping.class);
		grouping.setup(state, base.push(P_GROUPING));
	}

	public IJasimaGrouping getIndGrouping() {
		return grouping;
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		super.prepareToEvaluate(state, threadnum);

		// Reset the seed for the simulator.
		rotateSimSeed();

		// Set the new grouping scheme.
		grouping.clearForGeneration(state);
		grouping.groupIndividuals(state, threadnum);
	}

	@Override
	public void finishEvaluating(final EvolutionState state, final int threadnum) {
		super.finishEvaluating(state, threadnum);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (rule != null) {
			evaluateInd(state, ind, subpopulation, threadnum);
		}

		if (groupRule != null) {
			evaluateGroup(state, ind, grouping.getGroups(ind), subpopulation, threadnum);
		}
	}

	// TODO Notes:
	// * I need to add in the ability to not go into evaluateInd() or evaluateGroup().
	// * I need to be able to obtain the TWT and add it to the fitness for grouped.

	protected void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			JasimaGPConfig config = new JasimaGPConfig();
			config.setState(state);
			config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);

			rule.setConfiguration(config);

			for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
				Experiment experiment = getExperiment(state, rule, i, getWorkStationListeners(), getTracker());

				experiment.runExperiment();

				fitness.accumulateIndFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());

				rule.clear();
			}

			fitness.setIndFitness(state, getSimConfig(), (JasimaGPIndividual) ind);
			fitness.clearIndFitness();

			resetSimSeed();
		}
	}

	protected void evaluateGroup(final EvolutionState state,
			final Individual ind,
			final JasimaGroupedIndividual[] group,
			final int subpopulation,
			final int threadnum) {
		// Evaluate the groups one by one.
		for (int i = 0; i < group.length; i++) {
			if (!group[i].isEvaluated()) {
				JasimaGPConfig config = new JasimaGPConfig();
				config.setState(state);
				config.setIndividuals(group[i].getInds());
				config.setSubpopulations(new int[]{subpopulation});
				config.setThreadnum(threadnum);
				config.setData((JasimaGPData) input);
				if (hasTracker()) { config.setTracker(getTracker()); }

				groupRule.setConfiguration(config);

				for (int j = 0; j < getSimConfig().getNumConfigs(); j++) {
					Experiment experiment = getExperiment(state, groupRule, j, getWorkStationListeners(), getTracker());

					experiment.runExperiment();

					fitness.accumulateGroupFitness(i, getSimConfig(), (JasimaGPIndividual) ind, experiment.getResults());
					getTracker().clear();

					groupRule.clear();
				}

				resetSimSeed();
			}
		}

		// Set the fitnesses of the groups.
		for (int i = 0; i < group.length; i++) {
			if (!group[i].isEvaluated()) {
				fitness.setGroupFitness(state, getSimConfig(), (JasimaGPIndividual) ind, group[i]);
			}
		}

		// Clear the fitness from the groups.
		fitness.clearGroupFitness();
	}

	@Override
	public Object clone() {
		JasimaGroupedProblem newObject = (JasimaGroupedProblem)super.clone();

		newObject.rule = rule;
		newObject.groupRule = groupRule;
		newObject.fitness = fitness;
		newObject.grouping = grouping;

		return newObject;
	}

}
