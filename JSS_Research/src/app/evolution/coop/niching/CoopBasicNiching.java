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

public class CoopBasicNiching implements IJasimaCoopNiching {

	private static final long serialVersionUID = 1801607382835692874L;

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

			// Calculate the density of the individuals.
			double[] simAdjust = getAdjustments(state, distances);

			for (int j = 0; j < collaborators.length; j++) {
				adjustment[j] += simAdjust[j];
			}
		}

		// Adjust the fitnesses of the individuals according to the niching algorithm.
		for (int i = 0; i < collaborators.length; i++) {
			if (updateFitness[i]) {
				adjustment[i] = adjustment[i] / simConfig.getNumConfigs();

				KozaFitness fitness = (KozaFitness) collaborators[i].fitness;
				double standardisedFitness = fitness.standardizedFitness();
				double adjustedFitness = standardisedFitness * (1.0 + adjustment[i]);

				fitness.setStandardizedFitness(state, adjustedFitness);
			}
		}
	}

	protected double[] getAdjustments(final EvolutionState state, double[][] distances) {
		double[] simAdjust = new double[distances.length];

		for (int i = 0; i < distances.length; i++) {
			for (int j = 0; j < distances[i].length; j++) {
				simAdjust[i] += distances[i][j];
			}

			simAdjust[i] = simAdjust[i] / distances[i].length;
		}

		return simAdjust;
	}

	public void clear() {
		// Does nothing.
	}

}
