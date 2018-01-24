package app.evolution.niched.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.niched.JasimaNichedIndividual;
import app.evolution.niched.JasimaNichedProblem;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public class NormMWTBreakdownFitness extends NicheFitness {

	private static final int NOT_SET = -1;

//	private JasimaNichedIndividual[] nichedInds;
//	private double[] nichedIndFitness;
	private JasimaNichedProblem problem;
	
	private List<Integer> nicheIndex;
	private int numNiches = NOT_SET;

	private JasimaNichedIndividual currentInd = null;
	private double currentIndFitness = 0;

	@Override
	public void init(final EvolutionState state, final SimConfig config, final int threadnum) {
		if (numNiches == NOT_SET) {
			calculateNumNiches(config);
		}

		this.problem = (JasimaNichedProblem) state.evaluator.p_problem;
	}

	@Override
	public void updateArchive(final EvolutionState state, final JasimaNichedIndividual[] nichedInds, final SimConfig config, final int threadnum) {
		for (int i = 0; i < state.population.archive.length; i++) {
			// The individual's in the current generation archive have already been evaluated in the
			// niche specific training set, so use the fitnesses from those.
			JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) state.population.archive[i];

			if (nichedInd == null || nichedInds[i].getNichedFitness().betterThan(nichedInd.getNichedFitness())) {
				state.population.archive[i] = nichedInds[i];
			}
		}
	}

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaGPIndividual ind, Map<String, Object> results) {
		List<Double> referenceStat = getProblem().getReferenceStat();

		double normMwt = WeightedTardinessStat.getNormMeanWeightedTardiness(results, referenceStat.get(expIndex));

		// Individual hasn't been initialised yet.
		if (currentInd == null) {
			currentInd = (JasimaNichedIndividual) ind;
			currentIndFitness = 0.0;
		}

		currentIndFitness += normMwt;
		int currentNiche = nicheIndex.get(expIndex);

		// If its the last index for the specific niche,
		// then need to compare the fitness of the individual on the niche
		// to the current generation niched individual.
		if (expIndex == config.getNumConfigs() - 1 ||
				nicheIndex.get(expIndex) != nicheIndex.get(expIndex + 1)) {
			JasimaNichedIndividual[] nichedInds = problem.getCurGenNichedInds();
			double[] nichedIndsFitness = problem.getCurGenNichedIndsFitness();
			
			// Store the individual's fitness if the fitness is good enough.
			if (nichedInds[currentNiche] == null || nichedIndsFitness[currentNiche] > currentIndFitness) {
				nichedInds[currentNiche] = currentInd;
				nichedIndsFitness[currentNiche] = currentIndFitness;
			}

			currentInd = null;
			currentIndFitness = 0.0; // Just making sure.
		}

		return normMwt;
	}

	@Override
	public void setNichedFitness(final EvolutionState state, final SimConfig config, final JasimaNichedIndividual ind) {
		double finalFitness = getFinalFitness(state, config, ind);

		((KozaFitness) ind.getNichedFitness()).setStandardizedFitness(state, finalFitness);
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

		currentInd = null;
		currentIndFitness = 0;
	}

}
