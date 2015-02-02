package app.evolution;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import jss.evolution.IGroupedIndividual;
import jss.evolution.tracker.PriorityTracker;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaGroupedProblem extends GPProblem {

	private static final long serialVersionUID = -3817123526020178300L;

	public static final String P_RULE = "rule";
	public static final String P_FITNESS = "fitness";

	private AbsPriorityRule rule;
	private IJasimaFitness fitness;

	private IGroupedIndividual individualGrouping = null;
	private PriorityTracker tracker = null;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// Setup the GPData
		input = (JasimaGPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JasimaGPData.class);
		input.setup(state, base.push(P_DATA));

		// Setup the dataset and the solver
		rule = (AbsPriorityRule) state.parameters.getInstanceForParameterEq(base.push(P_RULE), null, AbsPriorityRule.class);
		fitness = (IJasimaFitness) state.parameters.getInstanceForParameterEq(base.push(P_FITNESS), null, IJasimaFitness.class);
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		if (!ind.evaluated) {
			JasimaGPConfiguration config = new JasimaGPConfiguration();
			config.setState(state);
			config.setIndividuals(new GPIndividual[]{(GPIndividual)ind});
			config.setSubpopulations(new int[]{subpopulation});
			config.setThreadnum(threadnum);
			config.setData((JasimaGPData)input);

			rule.setConfiguration(config);

			Experiment experiment = getExperiment(rule);

			experiment.runExperiment();
			experiment.getResults();

			fitness.setFitness(state, ind, experiment.getResults());

			ind.evaluated = true;
		}
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(AbsPriorityRule rule) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();
		experiment.setInitialSeed(15); // TODO temp seed.
		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public Object clone() {
		JasimaGroupedProblem newObject = (JasimaGroupedProblem)super.clone();

		newObject.input = (JasimaGPData)input.clone();
		newObject.rule = rule;
		newObject.fitness = fitness;

		return newObject;
	}

}
