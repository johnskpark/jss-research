package app.evolution.multilevel;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.multilevel.MLSProblemForm;
import ec.multilevel.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JasimaMultilevelProblem extends JasimaGPProblem implements MLSProblemForm {

	private static final long serialVersionUID = -5150181943760622786L;

	public static final String P_GROUP_RULE = "group-rule";
	public static final String P_GROUP_FITNESS = "group-fitness";

	public static final String P_IND_RULE = "ind-rule";
	public static final String P_IND_FITNESS = "ind-fitness";

	public static final String P_NICHING = "niching";

	private GPPriorityRuleBase groupRule;
	private JasimaMultilevelGroupFitness groupFitness;

	private GPPriorityRuleBase indRule;
	private JasimaMultilevelIndividualFitness indFitness;

	private IJasimaMultilevelNiching niching;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the individual solver for evaluating groups.
		groupRule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, GPPriorityRuleBase.class);
		getWorkStationListeners().add(groupRule);

		groupFitness = (JasimaMultilevelGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_FITNESS), null, JasimaMultilevelGroupFitness.class);
		groupFitness.setProblem(this);

		// Setup the individual solver for evaluating individuals.
		indRule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, GPPriorityRuleBase.class);
		getWorkStationListeners().add(indRule);

		indFitness = (JasimaMultilevelIndividualFitness) state.parameters.getInstanceForParameterEq(base.push(P_IND_FITNESS), null, JasimaMultilevelIndividualFitness.class);
		indFitness.setProblem(this);

		// Setup the niching algorithm.
		try {
			niching = (IJasimaMultilevelNiching) state.parameters.getInstanceForParameterEq(base.push(P_NICHING), null, IJasimaMultilevelNiching.class);
			niching.setup(state, base.push(P_NICHING));
		} catch (ParamClassLoadException ex) {
			state.output.warning("No niching algorithm provided for JasimaMultilevelProblem.");
		}
	}

	@Override
	public void beforeEvaluation(final EvolutionState state, final int threadnum, Population pop) {
		super.prepareToEvaluate(state, threadnum, groupRule);

		// Set the subpopulation to not being evaluated.
		for (Subpopulation subpop : pop.subpops) {
			((MLSSubpopulation) subpop).setEvaluated(false);
		}

		// Set the individuals to not being evaluated.
		for (Individual ind : pop.subpops[0].individuals) {
			ind.evaluated = false;
		}
	}

	@Override
	public void afterEvaluation(final EvolutionState state, final int threadnum, Population pop) {
		// Clear the niching method.
		if (niching != null) {
			niching.clear();
		}
		
		super.finishEvaluating(state, threadnum, groupRule);
	}

	public JasimaMultilevelGroupFitness getGroupFitness() {
		return groupFitness;
	}

	public JasimaMultilevelIndividualFitness getIndividualFitness() {
		return indFitness;
	}

	public IJasimaMultilevelNiching getNiching() {
		return niching;
	}

	@Override
	public void evaluateGroup(final EvolutionState state,
			final MLSSubpopulation group,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		if (!group.isEvaluated()) {
			configureRule(state, groupRule, getTracker(),
					group.individuals, subpops, threadnum);

			initialiseTracker(getTracker());

			for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
				Experiment experiment = getExperiment(state, groupRule, expIndex, getSimConfig(), getWorkStationListeners(), getTracker());
				experiment.runExperiment();
				groupFitness.accumulateFitness(expIndex, getSimConfig(), group, experiment.getResults());

				clearForExperiment(getWorkStationListeners());
			}

			groupFitness.setFitness(state, getSimConfig(), group);
			groupFitness.clear();

			// Add in the niching adjustment to the fitnesses.
			if (hasTracker() && niching != null) {
				niching.adjustFitness(state, getTracker(), group, groupRule);
			}

			group.setEvaluated(true);

			clearForRun(getTracker());
		}
	}

	@Override
	public void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			configureRule(state, indRule, null,
					new Individual[]{ ind }, new int[]{ subpopulation }, threadnum);

			initialiseTracker(null);

			for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
				Experiment experiment = getExperiment(state, indRule, expIndex, getSimConfig(), getWorkStationListeners(), null);
				experiment.runExperiment();
				indFitness.accumulateFitness(expIndex, getSimConfig(), (JasimaMultilevelIndividual) ind, experiment.getResults()); // getReferenceStat().get(expIndex));

				clearForExperiment(getWorkStationListeners());
			}

			indFitness.setFitness(state, getSimConfig(), (JasimaMultilevelIndividual) ind);
			indFitness.clear();

			ind.evaluated = true;

			clearForRun(null);
		}
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		state.output.warning("evaluateInd should be called instead of evaluate for JasimaMultilevelProblem.");

		evaluateInd(state, ind, subpopulation, threadnum);
	}

	@Override
	public Object clone() {
		JasimaMultilevelProblem newObject = (JasimaMultilevelProblem) super.clone();

		newObject.groupRule = groupRule;
		newObject.groupFitness = groupFitness;
		newObject.indRule = indRule;
		newObject.indFitness = indFitness;
		newObject.niching = niching;

		return newObject;
	}

}
