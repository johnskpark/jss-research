package app.evolution.coop;

import java.util.ArrayList;

import app.evolution.GPPriorityRuleBase;
import app.evolution.JasimaFitnessBase;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JasimaCoopProblem extends JasimaGPProblem implements GroupedProblemForm {

	private static final long serialVersionUID = -1068923215891516182L;

	public static final String P_IND_RULE = "ind-rule";
	public static final String P_IND_FITNESS = "ind-fitness";

	public static final String P_COOP_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	public static final String P_NICHING = "niching";

	private GPPriorityRuleBase indRule;
	private JasimaFitnessBase<JasimaCoopIndividual> indFitness;
	private boolean indRuleInitialised = false;

	private GPPriorityRuleBase coopRule;
	private JasimaCoopFitness fitness;

	private int numSubpops;

	private IJasimaCoopNiching niching;

	// TODO temporary.
	private SummaryStat allIndStats;
	private SummaryStat[] indStatPerSubpop;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the solver for individual rules.
		try {
			indRule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_IND_RULE), null, GPPriorityRuleBase.class);
			indFitness = (JasimaFitnessBase<JasimaCoopIndividual>) state.parameters.getInstanceForParameter(base.push(P_IND_FITNESS), null, JasimaFitnessBase.class);
			indFitness.setProblem(this);
			indRuleInitialised = true;
		} catch (ParamClassLoadException ex) {
			state.output.warning("Individual rule will not be used for this run.");
		}

		// Setup the solver.
		coopRule = (GPPriorityRuleBase) state.parameters.getInstanceForParameterEq(base.push(P_COOP_RULE), null, GPPriorityRuleBase.class);
		getWorkStationListeners().add(coopRule);

		// Setup the fitness.
		fitness = (JasimaCoopFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, JasimaCoopFitness.class);
		fitness.setProblem(this);

		// Setup the number of subpopulations.
        numSubpops = state.parameters.getInt((new Parameter(Initializer.P_POP)).push(Population.P_SIZE), null, 1);

        try {
        	niching = (IJasimaCoopNiching) state.parameters.getInstanceForParameterEq(base.push(P_NICHING), null, IJasimaCoopNiching.class);
			niching.setup(state, base.push(P_NICHING));
        } catch (ParamClassLoadException ex) {
        	state.output.warning("No niching algorithm provided for JasimaCoopProblem.");
        }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void preprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] prepareForFitnessAssessment,
			final boolean countVictoriesOnly) {
		super.prepareToEvaluate(state, 0, coopRule);

		for (int i = 0; i < pop.subpops.length; i++) {
			if (prepareForFitnessAssessment[i]) {
				for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
					Fitness fitness = pop.subpops[i].individuals[j].fitness;
					fitness.trials = new ArrayList();
				}
			}
		}

		if (indRuleInitialised()) {
			allIndStats = new SummaryStat();

        	indStatPerSubpop = new SummaryStat[state.population.subpops.length];
			for (int i = 0; i < indStatPerSubpop.length; i++) {
				indStatPerSubpop[i] = new SummaryStat();
			}
		}
	}

	@Override
	public void postprocessPopulation(final EvolutionState state,
			final Population pop,
			final boolean[] assessFitness,
			final boolean countVictoriesOnly) {
		super.finishEvaluating(state, 0, coopRule);
	}

	public SummaryStat getAllIndStats() {
		return allIndStats;
	}

	public SummaryStat[] getIndStatPerSubpop() {
		return indStatPerSubpop;
	}

	public boolean indRuleInitialised() {
		return indRuleInitialised;
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual[] inds,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		if (indRuleInitialised()) {
			for (int i = 0; i < inds.length; i++) {
				evaluateIndividuals(state, inds[i], i, updateFitness, countVictoriesOnly, subpops, threadnum);
			}
		}

		for (int i = 0; i < inds.length; i++) {
			((JasimaCoopIndividual) inds[i]).setCollaborators(inds);
		}
		fitness.loadIndividuals(inds);

		configureRule(state, coopRule, getTracker(), inds, subpops, threadnum);

		initialiseTracker(getTracker());

		for (int expIndex = 0; expIndex < getSimConfig().getNumConfigs(); expIndex++) {
			Experiment experiment = getExperiment(state, coopRule, expIndex, getWorkStationListeners(), getTracker());

			experiment.runExperiment();

			// Add in the results of the training instance to the fitness of the group.
			for (int j = 0; j < inds.length; j++) {
				fitness.accumulateFitness(expIndex, getSimConfig(), (JasimaCoopIndividual) inds[j], experiment.getResults(), j);
			}

			clearForExperiment(getWorkStationListeners());
		}

		fitness.setUpdateConfiguration(inds, updateFitness, shouldSetContext());
		for (int i = 0; i < inds.length; i++) {
			fitness.setFitness(state, getSimConfig(), (JasimaCoopIndividual) inds[i], i);
			inds[i].evaluated = true;
		}
		fitness.clear();

		if (hasTracker() && niching != null) {
			niching.adjustFitness(state, getTracker(), updateFitness, (JasimaCoopIndividual) inds[0], coopRule);
		}

		clearForRun(getTracker());
	}

	public void evaluateIndividuals(final EvolutionState state,
			final Individual ind,
			final int index,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		configureRule(state, indRule, null, new Individual[]{ ind }, subpops, threadnum);

		initialiseTracker(null);

		for (int i = 0; i < getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(state, indRule, i, getWorkStationListeners(), getTracker());
			experiment.runExperiment();

			indFitness.accumulateFitness(i, getSimConfig(), (JasimaCoopIndividual) ind, experiment.getResults());

			clearForExperiment(getWorkStationListeners());
		}

		double finalFitness = indFitness.getFinalFitness(state, getSimConfig(), (JasimaCoopIndividual) ind);

		allIndStats.value(finalFitness);
		indStatPerSubpop[index].value(finalFitness);
		indFitness.clear();

		clearForRun(null);
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
		newObject.niching = niching;

		return newObject;
	}

}
