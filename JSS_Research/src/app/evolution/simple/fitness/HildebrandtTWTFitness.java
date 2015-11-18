package app.evolution.simple.fitness;

import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.JasimaGPProblem;
import app.evolution.simple.IJasimaSimpleFitness;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public class HildebrandtTWTFitness implements IJasimaSimpleFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";
	private static final String NJ_FINISHED_STR = "numJobsFinished";

	private static final double MIN_PENALTY_RATIO = 0.9;

	private SummaryStat perfIndexStat = new SummaryStat();
	private SummaryStat fullSysPenStat = new SummaryStat();

	private JasimaGPProblem problem;

	private double[] benchmarkTWTs;
	private int[] benchmarkJobsFinished;

	@Override
	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;

		setupBenchmark();
	}

	private void setupBenchmark() {
		benchmarkTWTs = new double[problem.getSimConfig().getNumConfigs()];
		benchmarkJobsFinished = new int[problem.getSimConfig().getNumConfigs()];

		BenchmarkRule rule = new BenchmarkRule();
		for (int i = 0; i < problem.getSimConfig().getNumConfigs(); i++) {
			Experiment experiment = getExperiment(rule, i);
			experiment.runExperiment();

			Map<String, Object> results = experiment.getResults();

			benchmarkTWTs[i] = ((SummaryStat) results.get(WT_MEAN_STR)).sum();
			benchmarkJobsFinished[i] = (Integer) results.get(NJ_FINISHED_STR);
		}
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(PR rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(problem.getSimConfig().getLongValue());
		experiment.setNumMachines(problem.getSimConfig().getNumMachines(index));
		experiment.setUtilLevel(problem.getSimConfig().getUtilLevel(index));
		experiment.setDueDateFactor(problem.getSimConfig().getDueDateFactor(index));
		experiment.setWeights(problem.getSimConfig().getWeight(index));
		experiment.setOpProcTime(problem.getSimConfig().getMinOpProc(index), problem.getSimConfig().getMaxOpProc(index));
		experiment.setNumOps(problem.getSimConfig().getMinNumOps(index), problem.getSimConfig().getMaxNumOps(index));

		experiment.setShopListener(new NotifierListener[]{new BasicJobStatCollector()});
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	@Override
	public void accumulateFitness(final int index, final Map<String, Object> results) {
		SummaryStat stat = (SummaryStat) results.get(WT_MEAN_STR);
		int jobsFinished = (Integer) results.get(NJ_FINISHED_STR);

		perfIndexStat.value(benchmarkTWTs[index] / stat.sum());
		fullSysPenStat.value(1.0 / (Math.min(MIN_PENALTY_RATIO, 1.0 * jobsFinished / benchmarkJobsFinished[index])));
	}

	@Override
	public void setFitness(final EvolutionState state,
			final JasimaGPIndividual ind) {
		double fitness = perfIndexStat.sum() * fullSysPenStat.sum();
		((KozaFitness) ind.fitness).setStandardizedFitness(state, fitness);
	}

	@Override
	public void clear() {
		perfIndexStat.clear();
		fullSysPenStat.clear();
	}

	private class BenchmarkRule extends PR {

		private static final long serialVersionUID = 4224225415226813544L;

		@Override
		public double calcPrio(PrioRuleTarget entry) {
			double pt = entry.currProcTime();
			double winq = 0;
			double npt = 0;

			int nextTask = entry.getTaskNumber() + 1;
			if (nextTask < entry.numOps()) {
				WorkStation nextMachine = entry.getOps()[nextTask].machine;
				winq = nextMachine.workContent(false);
				npt = entry.getOps()[nextTask].procTime;
			}

			return -(2 * pt + winq + npt);
		}
	}

}
