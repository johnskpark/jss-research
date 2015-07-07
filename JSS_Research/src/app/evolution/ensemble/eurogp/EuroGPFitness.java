package app.evolution.ensemble.eurogp;

import jasima.core.experiment.Experiment;
import jasima.core.statistics.SummaryStat;

import java.util.HashMap;
import java.util.Map;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public class EuroGPFitness {

	private static final String WT_MEAN_STR = "weightedTardMean";

	private Map<GPIndividual, FitnessTracker> fitnessMap = new HashMap<GPIndividual, FitnessTracker>();

	public void loadIndividuals(final GPIndividual[] gpInds) {
		for (int i = 0; i < gpInds.length; i++) {
			fitnessMap.put(gpInds[i], new FitnessTracker(gpInds[i]));
		}
	}

	public void accumulateFitness(final GPIndividual[] gpInds,
			Experiment experiment,
			EuroGPTracker tracker) {
		double performance = ((SummaryStat) experiment.getResults().get(WT_MEAN_STR)).sum();

		for (int i = 0; i < gpInds.length; i++) {
			fitnessMap.get(gpInds).addPerformance(performance);
			fitnessMap.get(gpInds).addDiversity(0);
		}

		// TODO

		accumulatePerformance();
		accumulateDiversity();
	}

	private void accumulatePerformance() {
		// TODO
	}

	private void accumulateDiversity() {
		// TODO
	}

	public void setFitness(final EvolutionState state,
			final Individual[] inds,
			boolean[] updateFitness,
			boolean shouldSetContext) {
		// TODO
	}

	public void clear() {
		fitnessMap.clear();
	}

	private class FitnessTracker {
		private GPIndividual gpInd;

		private double performance = 0.0;
		private double diversity = 0.0;

		public FitnessTracker(GPIndividual gpInd) {
			this.gpInd = gpInd;
		}

		public void addPerformance(double perf) {
			performance += perf;
		}

		public void addDiversity(double div) {
			diversity += div;
		}

		public double getPerformance() {
			return performance;
		}

		public double getDiversity() {
			return diversity;
		}
	}

}
