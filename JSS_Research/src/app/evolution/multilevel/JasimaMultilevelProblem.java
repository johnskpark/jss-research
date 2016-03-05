package app.evolution.multilevel;

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
// TODO need to record the diversity measures.
public class JasimaMultilevelProblem extends JasimaGPProblem implements MLSProblemForm {

	private static final long serialVersionUID = -5150181943760622786L;

	public static final String P_GROUP_RULE = "group-rule";
	public static final String P_GROUP_FITNESS = "group-fitness";

	public static final String P_IND_RULE = "ind-rule";
	public static final String P_IND_FITNESS = "ind-fitness";

	public static final String P_NICHING = "niching";

	private AbsGPPriorityRule groupRule;
	private JasimaMultilevelGroupFitness groupFitness;

	private AbsGPPriorityRule indRule;
	private JasimaMultilevelIndividualFitness indFitness;

	private IJasimaMultilevelNiching niching;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the individual solver for evaluating groups.
		groupRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_RULE), null, AbsGPPriorityRule.class);
		groupFitness = (JasimaMultilevelGroupFitness) state.parameters.getInstanceForParameterEq(base.push(P_GROUP_FITNESS), null, JasimaMultilevelGroupFitness.class);
		groupFitness.setProblem(this);

		// Setup the individual solver for evaluating individuals.
		indRule = (AbsGPPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, AbsGPPriorityRule.class);
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
	public void beforeEvaluation(final EvolutionState state, Population pop) {
		// Reset the seed for the simulator.
		rotateSimSeed();

		// Set the subpopulation to not being evaluated.
		for (Subpopulation subpop : pop.subpops) {
			((MLSSubpopulation) subpop).setEvaluated(false);
		}

		// Setup the tracker.
		if (hasTracker()) {
			getTracker().setPriorityRule(groupRule);
			getTracker().setSimConfig(getSimConfig());
		}

		// Apply the benchmark/reference rule to the problem instances.
		if (hasReferenceRule()) {
			evaluateReference();
		}
	}

	@Override
	public void afterEvaluation(final EvolutionState state, Population pop) {
		// Clear the niching method.
		if (niching != null) {
			niching.clear();
		}

		// Clear the reference stat.
		clearReference();
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
		// We don't care if the group's been evaluated previously,
		// since the simulation changes at each generation.
		List<GPIndividual> indsList = new ArrayList<GPIndividual>();
		for (Individual ind : group.individuals) { indsList.add((GPIndividual) ind); }

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

		resetSimSeed();

		groupFitness.setFitness(state, group);
		groupFitness.clear();

		// Add in the niching adjustment to the fitnesses.
		if (hasTracker()) {
			if (niching != null) {
				niching.adjustFitness(state, getTracker(), group);
			}
			getTracker().clear();
		}

		group.setEvaluated(true);
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

			indFitness.accumulateFitness(expIndex, (JasimaMultilevelIndividual) ind, experiment.getResults()); // getReferenceStat().get(expIndex));
			if (hasWorkStationListener()) { getWorkStationListener().clear(); }
		}

		indFitness.setFitness(state, (JasimaMultilevelIndividual) ind);
		indFitness.clear();

		ind.evaluated = true;

		resetSimSeed();
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
