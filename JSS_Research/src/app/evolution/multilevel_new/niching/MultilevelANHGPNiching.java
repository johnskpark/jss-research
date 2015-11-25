package app.evolution.multilevel_new.niching;

import java.util.List;

import app.evolution.multilevel_new.IJasimaMultilevelNiching;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaEvolveExperimentTracker;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

public abstract class MultilevelANHGPNiching implements IJasimaMultilevelNiching {

	@Override
	public void adjustFitness(final EvolutionState state, 
			final JasimaEvolveExperimentTracker tracker,
			final MLSSubpopulation group) {
		List<JasimaEvolveExperiment> experiments = tracker.getResults();
		AbsSimConfig simConfig = tracker.getSimConfig();
		
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			// Calculate the distances between the individuals.
			double[][] distances = getDistances(state, experiments.get(i), simConfig, group);
			
			// Calculate the sharing function values.
			double[][] sharingValues = getSharingValues(state, distances);
			
			// Calculate the density of the individuals.
			double[] density = getDensity(state, sharingValues);
		}
	}
	
	/**
	 * TODO javadoc.
	 */
	public abstract double[][] getDistances(final EvolutionState state, 
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final MLSSubpopulation group);
	
	/**
	 * TODO javadoc.
	 */
	public double[][] getSharingValues(final EvolutionState state, double[][] distances) {
		double[][] sharingValues = new double[distances.length][distances[0].length];
		
		// Calculate the sharing function values. 
		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances[0].length; j++) {
				double closeDegree = getCloseDegree(state);
				
				if (distances[i][j] <= closeDegree) {
					sharingValues[i][j] = 1.0 - (distances[i][j] / closeDegree);
				} else {
					sharingValues[i][j] = 0.0;	
				}
			}
		}
		
		return sharingValues;
	}
	
	/**
	 * TODO javadoc.
	 */
	public double[] getDensity(final EvolutionState state, double[][] sharingValues) {
		double[] density = new double[sharingValues.length];
		
		// Calculate the densities.
		for (int i = 0; i < sharingValues.length; i++) {
			for (int j = 0; j < sharingValues[i].length; j++) {
				density[i] += sharingValues[i][j];
			}
			
			density[i] = density[i] / sharingValues[i].length;
		}
		
		return density;
	}
	
	/**
	 * TODO javadoc.
	 */
	public double getCloseDegree(final EvolutionState state) {
		return 1.0;
	}

}
