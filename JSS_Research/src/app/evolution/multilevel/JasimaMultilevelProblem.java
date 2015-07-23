package app.evolution.multilevel;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.Random;

import app.evolution.AbsGPPriorityRule;
import app.evolution.IJasimaGPProblem;
import app.listener.hunt.HuntListener;
import app.simConfig.AbsSimConfig;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPProblem;
import ec.util.Parameter;

public class JasimaMultilevelProblem extends GPProblem implements GroupedProblemForm, IJasimaGPProblem {

	private static final long serialVersionUID = -5150181943760622786L;

	private AbsSimConfig simConfig;
	private Random rand;
	private long simSeed;
	private int numSubpops;

	private HuntListener huntListener = new HuntListener(5);

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		// TODO
	}

	@Override
	public void preprocessPopulation(EvolutionState state, Population pop,
			boolean[] prepareForFitnessAssessment, boolean countVictoriesOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postprocessPopulation(EvolutionState state, Population pop,
			boolean[] assessFitness, boolean countVictoriesOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(final EvolutionState state, AbsGPPriorityRule rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.addMachineListener(huntListener);
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum) {
		state.output.fatal("JasimaMultilevelProblem must be used in a grouped problem form");
	}

	@Override
	public AbsSimConfig getSimConfig() {
		return simConfig;
	}

	@Override
	public int getNumInds() {
		return numSubpops;
	}

}
