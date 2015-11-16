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

	private AbsGPPriorityRule groupRule;
	private IJasimaMultilevelGroupFitness groupFitness;

	private AbsGPPriorityRule indRule;
	private IJasimaMultilevelIndividualFitness indFitness;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the individual solver for evaluating groups.
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, AbsGPPriorityRule.class);
		groupFitness = (IJasimaMultilevelGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_FITNESS), null, IJasimaMultilevelGroupFitness.class);

		// Setup the individual solver for evaluating individuals.
		indRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, AbsGPPriorityRule.class);
		indFitness = (IJasimaMultilevelIndividualFitness) state.parameters.getInstanceForParameterEq(base.push(P_IND_FITNESS), null, IJasimaMultilevelIndividualFitness.class);
	}

	@Override
	public void beforeEvaluation(final EvolutionState state, Population pop) {
		// Reset the seed for the simulator.
		getSimConfig().setSeed(getRandom().nextLong());

		// Set the subpopulation to not being evaluated.
		for (Subpopulation subpop : pop.subpops) {
			((MLSSubpopulation) subpop).setEvaluated(false);
		}
	}

	@Override
	public void evaluateGroup(final EvolutionState state,
			final MLSSubpopulation subpop,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// We don't care if the group's been evaluated previously,
		// since the simulation changes at each generation.
		List<GPIndividual> indsList = new ArrayList<GPIndividual>();
		for (Individual ind : subpop.individuals) {
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

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, groupRule, expIndex);

			experiment.runExperiment();

			groupFitness.accumulateFitness(expIndex, subpop, experiment.getResults());
			if (hasTracker()) {
				
				getTracker().clear();
			}
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		groupFitness.setFitness(state, subpop, updateFitness, shouldSetContext());
		groupFitness.clear();

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
			Experiment experiment = getExperiment(state, indRule, expIndex);

			experiment.runExperiment();

			indFitness.accumulateFitness(expIndex, experiment.getResults());
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		indFitness.setFitness(state, ind);
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
