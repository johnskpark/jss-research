package app.evolution.coop.niching;

import java.util.Arrays;
import java.util.List;

import app.evolution.coop.IJasimaCoopNiching;
import app.evolution.coop.JasimaCoopIndividual;
import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaEvolveExperimentTracker;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;

public class CoopANHGPNiching implements IJasimaCoopNiching {

	private static final long serialVersionUID = -1453074618976690865L;

	public static final String P_DISTANCE = "distance";

	public static final double LEARNING_RATE = 0.5;

	private DistanceMeasure measure;

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			measure = (DistanceMeasure) state.parameters.getInstanceForParameterEq(base.push(P_DISTANCE), null, DistanceMeasure.class);
		} catch (ParamClassLoadException ex) {
			state.output.fatal("The distance measure was not correctly initialised for MultilevelANHGPNiching.");
		}
	}

	@Override
	public void adjustFitness(EvolutionState state,
			JasimaEvolveExperimentTracker tracker,
			JasimaCoopIndividual individual) {
		boolean[] updateFitness = new boolean[individual.getCollaborators().length];
		Arrays.fill(updateFitness, true);

		adjustFitness(state, tracker, updateFitness, individual);
	}

	@Override
	public void adjustFitness(final EvolutionState state,
			final JasimaEvolveExperimentTracker tracker,
			final boolean[] updateFitness,
			final JasimaCoopIndividual individual) {
		List<JasimaEvolveExperiment> experiments = tracker.getResults();
		AbsSimConfig simConfig = tracker.getSimConfig();

		Individual[] collaborators = individual.getCollaborators();

		double[] adjustment = new double[collaborators.length];

		// Calculate the adjustment from the individual's density.
		for (int i = 0; i < simConfig.getNumConfigs(); i++) {
			// Calculate the distances between the individuals.
			double[][] distances = measure.getDistances(state, experiments.get(i), simConfig, collaborators);

			// Calculate the sharing function values.
			double[][] sharingValues = getSharingValues(state, distances);

			// Calculate the density of the individuals.
			double[] density = getDensity(state, sharingValues);

			for (int j = 0; j < collaborators.length; j++) {
				adjustment[j] += density[j];
			}
		}

		// Adjust the fitnesses of the individuals according to the niching algorithm.
		for (int i = 0; i < collaborators.length; i++) {
			if (!updateFitness[i]) { continue; }

			adjustment[i] = adjustment[i] / simConfig.getNumConfigs();

			KozaFitness fitness = (KozaFitness) collaborators[i].fitness;
			double standardisedFitness = fitness.standardizedFitness();
			double adjustedFitness = standardisedFitness / adjustment[i];

			fitness.setStandardizedFitness(state, adjustedFitness);
		}
	}

	public double[][] getSharingValues(final EvolutionState state, double[][] distances) {
		double[][] sharingValues = new double[distances.length][distances.length];

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

	public double[] getDensity(final EvolutionState state, double[][] sharingValues) {
		double[] density = new double[sharingValues.length];

		for (int i = 0; i < sharingValues.length; i++) {
			for (int j = 0; j < sharingValues[i].length; j++) {
				density[i] += sharingValues[i][j];
			}

			density[i] = density[i] / sharingValues[i].length;
		}

		return density;
	}

	public double getCloseDegree(final EvolutionState state) {
		return LEARNING_RATE / state.generation;
	}

	public void clear() {
		// Does nothing.
	}

}
