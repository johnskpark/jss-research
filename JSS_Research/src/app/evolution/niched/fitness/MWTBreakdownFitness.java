package app.evolution.niched.fitness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.evolution.JasimaGPIndividual;
import app.evolution.niched.JasimaNichedIndividual;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.stat.WeightedTardinessStat;
import ec.EvolutionState;

public class MWTBreakdownFitness extends NicheFitness {

	private JasimaNichedIndividual[] nichedInds;
	private double[] nicheIndFitness;
	private List<Integer> nicheIndex;
	private int numNiches = 0;

	private JasimaNichedIndividual currentInd = null;
	private double currentIndFitness = 0;

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

		// FIXME temporary code to test to make sure the correct number of niches are being used
		// It should be 9 for HolthausSimConfig, 7 for HolthausSimConfig4.
		System.out.println(numNiches);

		numNiches++; // Account for the best individual being part of a niche as well.
		nichedInds = new JasimaNichedIndividual[numNiches];
		nicheIndFitness = new double[numNiches];
		for (int i = 0; i < nicheIndFitness.length; i++) {
			nicheIndFitness[i] = Double.POSITIVE_INFINITY;
		}
	}

	@Override
	public void finalise(final EvolutionState state, final SimConfig config, final int threadnum) {
		// The fitnesses of the individuals in the population.
		for (int i = 0; i < state.population.archive.length; i++) {
			// The individual's in the current generation archive have already been evaluated in the 
			// niche specific training set, so use the fitnesses from those.	
			JasimaNichedIndividual nichedInd = (JasimaNichedIndividual) state.population.archive[i];
			
			if (nichedInds[i].getNichedFitness().betterThan(nichedInd.getNichedFitness())) {
				state.population.archive[i] = nichedInds[i];
			}
		}
	}

	@Override
	public double getFitness(int expIndex, SimConfig config, JasimaGPIndividual ind, Map<String, Object> results) {
		double mwt = WeightedTardinessStat.getMeanWeightedTardiness(results);

		// Individual hasn't been initialised yet. 
		if (currentInd == null) {
			currentInd = (JasimaNichedIndividual) ind;
			currentIndFitness = 0.0;
		}
		
		currentIndFitness += mwt;
		int currentNiche = nicheIndex.get(expIndex);
		
		// If its the last index for the specific niche, 
		// then need to compare the fitness of the individual on the niche 
		// to the current generation niched individual. 
		if (expIndex == config.getNumConfigs() - 1 || 
				nicheIndex.get(expIndex) != nicheIndex.get(expIndex + 1)) {
			// Store the individual's fitness if the fitness is good enough. 
			if (nichedInds[currentNiche] == null || nicheIndFitness[currentNiche] > currentIndFitness) {
				nichedInds[currentNiche] = currentInd;
				nicheIndFitness[currentNiche] = currentIndFitness;
			}
			
			currentInd = null;
			currentIndFitness = 0.0; // Just making sure. 
		}
		
		return mwt;
	}

	public JasimaGPIndividual[] getNichedIndividuals() {
		return nichedInds;
	}

}
