package app.evolution.multilevel_new;

import jasima.core.experiment.Experiment;

import java.util.ArrayList;
import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.multilevel_new.MLSProblemForm;
import ec.multilevel_new.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
// TODO need to add in niching at some point.
public class JasimaMultilevelProblem extends JasimaGPProblem implements MLSProblemForm {

	private static final long serialVersionUID = -5150181943760622786L;

	public static final String P_GROUP_RULE = "group-rule";
	public static final String P_GROUP_FITNESS = "group-fitness";

	public static final String P_IND_RULE = "ind-rule";
	public static final String P_IND_FITNESS = "ind-fitness";

	public static final String P_NICHING = "niching";

	private AbsGPPriorityRule groupRule;
	private IJasimaMultilevelGroupFitness groupFitness;

	private AbsGPPriorityRule indRule;
	private IJasimaMultilevelIndividualFitness indFitness;

	private IJasimaMultilevelNiching niching;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the individual solver for evaluating groups.
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, AbsGPPriorityRule.class);
		groupFitness = (IJasimaMultilevelGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_FITNESS), null, IJasimaMultilevelGroupFitness.class);

		// Setup the individual solver for evaluating individuals.
		indRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, AbsGPPriorityRule.class);
		indFitness = (IJasimaMultilevelIndividualFitness) state.parameters.getInstanceForParameterEq(base.push(P_IND_FITNESS), null, IJasimaMultilevelIndividualFitness.class);

		// Setup the niching algorithm.
		try {
			niching = (IJasimaMultilevelNiching) state.parameters.getInstanceForParameterEq(base.push(P_NICHING), null, IJasimaMultilevelNiching.class);
		} catch (ParamClassLoadException ex) {
			state.output.warning("No niching algorithm provided for JasimaMultilevelProblem.");
		}
	}

	@Override
	public void beforeEvaluation(final EvolutionState state, Population pop) {
		// Reset the seed for the simulator.
		getSimConfig().setSeed(getRandom().nextLong());

		// Set the subpopulation to not being evaluated.
		for (Subpopulation subpop : pop.subpops) {
			((MLSSubpopulation) subpop).setEvaluated(false);
		}

		// Setup the tracker.
		if (hasTracker()) {
			getTracker().setPriorityRule(groupRule);
			getTracker().setSimConfig(getSimConfig());
		}
	}

	@Override
	public void evaluateGroup(final EvolutionState state,
			final MLSSubpopulation group,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// We don't care if the group's been evaluated previously,
		// since the simulation changes at each generation.
		List<GPIndividual> indsList = new ArrayList<GPIndividual>();
		for (Individual ind : group.individuals) {
			if (ind == null) {
				continue;
			}

			indsList.add((GPIndividual) ind);
		}

		GPIndividual[] gpInds = new GPIndividual[indsList.size()];
		indsList.toArray(gpInds);

		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(gpInds);
		config.setSubpopulations(subpops);
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);
		if (hasTracker()) { config.setTracker(getTracker()); }

		groupRule.setConfiguration(config);

		if (hasTracker()) { getTracker().initialise(); }

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, groupRule, expIndex, getWorkStationListener(), getTracker());

			experiment.runExperiment();

			// Add in the results of the training instance to the fitness of the group.
			groupFitness.accumulateFitness(expIndex, group, experiment.getResults());

			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		groupFitness.setFitness(state, group);
		groupFitness.clear();

		// Add in the niching adjustment to the fitnesses.
		if (hasTracker()) {
			if (niching != null) {
				niching.adjustFitness(state, getTracker(), group);
			}
			getTracker().clear();
		}

		getSimConfig().resetSeed();
	}

	@Override
	public void evaluateInd(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		// We don't care if the individual's been evaluated previously,
		// since the simulation changes at each generation.
		JasimaGPConfig config = new JasimaGPConfig();
		config.setState(state);
		config.setIndividuals(new GPIndividual[]{(GPIndividual) ind});
		config.setSubpopulations(new int[]{subpopulation});
		config.setThreadnum(threadnum);
		config.setData((JasimaGPData) input);

		indRule.setConfiguration(config);

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, indRule, expIndex, getWorkStationListener(), null);

			experiment.runExperiment();

			indFitness.accumulateFitness(expIndex, experiment.getResults());
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		indFitness.setFitness(state, (JasimaMultilevelIndividual) ind);
		indFitness.clear();

		getSimConfig().resetSeed();
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

		return newObject;
	}

}
