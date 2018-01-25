package app.evolution.niched.fitness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.niched.JasimaNichedIndividual;
import app.evolution.niched.JasimaNichedProblem;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;

public class MWTBreakdownFitness extends NicheFitnessBase {

	private static final int NOT_SET = -1;

//	private JasimaNichedIndividual[] nichedInds;
//	private double[] nichedIndFitness;
	private JasimaNichedProblem problem;

	private List<Integer> nicheIndex;
	private int numNiches = NOT_SET;

//	private JasimaNichedIndividual currentInd = null;
//	private double currentIndFitness = 0;

	@Override
	public void init(final EvolutionState state, final SimConfig config, final int threadnum) {
		if (numNiches == NOT_SET) {
			calculateNumNiches(config);
		}

		this.problem = (JasimaNichedProblem) state.evaluator.p_problem;
	}

	@Override
	public double getFitness(final int expIndex,
			final SimConfig config,
			final JasimaGPIndividual ind,
			final Map<String, Object> results) {
		double mwt = WeightedTardinessStat.getMeanWeightedTardiness(results);

//		// Individual hasn't been initialised yet.
//		if (currentInd == null) {
//			currentInd = (JasimaNichedIndividual) ind;
//			currentIndFitness = 0.0;
//		}
//
//		currentIndFitness += mwt;
//		int currentNiche = nicheIndex.get(expIndex);
//
//		// If its the last index for the specific niche,
//		// then need to compare the fitness of the individual on the niche
//		// to the current generation niched individual.
//		if (expIndex == config.getNumConfigs() - 1 ||
//				nicheIndex.get(expIndex) != nicheIndex.get(expIndex + 1)) {
//			JasimaNichedIndividual[] nichedInds = problem.getCurGenNichedInds();
//			double[] nichedIndsFitness = problem.getCurGenNichedIndsFitness();
//
//			// Store the individual's fitness if the fitness is good enough.
//			if (nichedInds[currentNiche] == null || nichedIndsFitness[currentNiche] > currentIndFitness) {
//				nichedInds[currentNiche] = currentInd;
//				nichedIndsFitness[currentNiche] = currentIndFitness;
//			}
//
//			currentInd = null;
//			currentIndFitness = 0.0; // Just making sure.
//		}

		return mwt;
	}

	// Update the current generation niched individuals.
	public void updateNiches(final EvolutionState state, final SimConfig config, final JasimaNichedIndividual ind) {
		List<Double> fitnesses = getInstanceFitnesses();

		double[] nichedFitnesses = new double[numNiches];
		Arrays.fill(nichedFitnesses, 0.0);

		for (int i = 0; i < fitnesses.size(); i++) {
			int index = nicheIndex.get(i);
			nichedFitnesses[index] += fitnesses.get(i);
		}

		JasimaNichedIndividual[] nichedInds = problem.getCurGenNichedInds();
		double[] nichedIndsFitness = problem.getCurGenNichedIndsFitness();

		for (int i = 0; i < numNiches; i++) {
			if (nichedInds[i] == null || nichedFitnesses[i] < nichedIndsFitness[i]) {
				nichedInds[i] = ind;
				nichedIndsFitness[i] = nichedFitnesses[i];
			}
		}
	}

	public int getNumNiches(SimConfig config) {
		if (numNiches == NOT_SET) {
			calculateNumNiches(config);
		}

		return numNiches;
	}

	public void calculateNumNiches(SimConfig config) {
		DynamicBreakdownSimConfig cfg = (DynamicBreakdownSimConfig) config;

		nicheIndex = new ArrayList<Integer>();
		numNiches = 0;

		double breakdownLevel = Double.NaN;
		double repairTimeFactor = Double.NaN;

		for (int i = 0; i < cfg.getNumConfigs(); i++) {
			if (breakdownLevel != cfg.getBreakdownLevel(i) || repairTimeFactor != cfg.getMeanRepairTime(i)) {
				breakdownLevel = cfg.getBreakdownLevel(i);
				repairTimeFactor = cfg.getMeanRepairTime(i);

				numNiches++;
			}

			nicheIndex.add(numNiches-1);
		}
	}

	@Override
	public void clear() {
		super.clear();

//		currentInd = null;
//		currentIndFitness = 0;
	}

}
