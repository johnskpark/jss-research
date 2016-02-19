package app.evolution.multilevel.niching;

import java.util.ArrayList;
import java.util.List;

import app.evolution.multilevel.IJasimaMultilevelFitnessListener;
import app.evolution.multilevel.IJasimaMultilevelNiching;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaEvolveExperimentTracker;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.gp.koza.KozaFitness;
import ec.multilevel.MLSSubpopulation;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

public class MultilevelANHGPNiching implements IJasimaMultilevelNiching {

	private static final long serialVersionUID = -8217196096385497137L;

	public static final String P_DISTANCE = "distance";

	public static final double LEARNING_RATE = 0.5;

	private DistanceMeasure measure;

	private List<IJasimaMultilevelFitnessListener> listeners = new ArrayList<IJasimaMultilevelFitnessListener>();

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			measure = (DistanceMeasure) state.parameters.getInstanceForParameterEq(base.push(P_DISTANCE), null, DistanceMeasure.class);
		} catch (ParamClassLoadException ex) {
			state.output.fatal("The distance measure was not correctly initialised for MultilevelANHGPNiching.");
		}
	}

	@Override
	public void addListener(IJasimaMultilevelFitnessListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void clearListeners() {
		listeners.clear();
	}

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveExperimentTracker tracker,
			final MLSSubpopulation group) {
		List<JasimaEvolveExperiment> experiments = tracker.getResults();
		AbsSimConfig simConfig = tracker.getSimConfig();

		double[] adjustment = new double[group.individuals.length];

		// Calculate the adjustment from the individual's density.
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			// Calculate the distances between the individuals.
			double[][] distances = measure.getDistances(state, experiments.get(i), simConfig, group.individuals);

			// Calculate the sharing function values.
			double[][] sharingValues = getSharingValues(state, distances);

			// Calculate the density of the individuals.
			double[] density = getDensity(state, sharingValues);

			for (int j = 0; j < group.individuals.length; j++) {
				adjustment[j] += density[j];
			}
		}

		// Adjust the fitnesses of the individuals according to the niching algorithm.
		for (int i = 0; i < group.individuals.length; i++) {
			adjustment[i] = adjustment[i] / simConfig.getNumConfigs();

			KozaFitness fitness = (KozaFitness) group.individuals[i].fitness;
			double standardisedFitness = fitness.standardizedFitness();
			double adjustedFitness = standardisedFitness / adjustment[i];

			fitness.setStandardizedFitness(state, adjustedFitness);
		}
	}

	/**
	 * TODO javadoc.
	 */
	public double[][] getSharingValues(final EvolutionState state, double[][] distances) {
		double[][] sharingValues = new double[distances.length][distances[0].length];

		// Calculate the sharing function values.
		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances[i].length; j++) {
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
		return LEARNING_RATE / state.generation;
	}

	public void clear() {
		// Does nothing.
	}

}
