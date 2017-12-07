package app.evolution.niched.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;

public class MWTBreakdownFitness extends NicheFitness {

	private JasimaGPIndividual[] nicheInds;
	private double[] nicheIndFitness;
	private List<Integer> nicheIndex;
	private int numNiches = 0;

	private JasimaGPIndividual currentInd = null;
	private double currentIndFitness = 0;
	private double currentBL = Double.NaN;
	private double currentMRT = Double.NaN;

	@Override
	public void init(final EvolutionState state, final SimConfig config, final int threadnum) {
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
				nicheIndex.add(i, numNiches-1);
			}
		}

		// TODO temporary code to test to make sure the correct number of niches are being used
		// It should be 9 for HolthausSimConfig, 7 for HolthausSimConfig4.
		System.out.println(numNiches);

		numNiches++; // Account for the best individual being part of a niche as well.
		nicheInds = new JasimaGPIndividual[numNiches];
		nicheIndFitness = new double[numNiches];
		for (int i = 0; i < nicheIndFitness.length; i++) {
			nicheIndFitness[i] = Double.POSITIVE_INFINITY;
		}
	}

	@Override
	public void finalise(final EvolutionState state, final SimConfig config, final int threadnum) {
		// TODO Compare the niche to the archive.

	}

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaGPIndividual ind, Map<String, Object> results) {
		double mwt = WeightedTardinessStat.getMeanWeightedTardiness(results);

		// TODO need to add in the fact that the current generation archive is updated here.
		// How do I figure out what is what, exactly?
		// Oh right, store a temporary variable or something, got it.
		DynamicBreakdownSimConfig cfg = (DynamicBreakdownSimConfig) config;

		if (currentInd == null || !currentInd.equals(ind)) {
			currentInd = ind;
			currentIndFitness = 0.0;
			currentBL = cfg.getBreakdownLevel(expIndex);
			currentMRT = cfg.getMeanRepairTime(expIndex);
		} else if (currentBL != cfg.getBreakdownLevel(expIndex) || currentMRT != cfg.getMeanRepairTime(expIndex)) {
			// I need to store what indices they correspond to as well. Shucks.

			currentIndFitness = 0.0;
			currentBL = cfg.getBreakdownLevel(expIndex);
			currentMRT = cfg.getMeanRepairTime(expIndex);
		}

		currentIndFitness += mwt;

		return mwt;
	}


}
