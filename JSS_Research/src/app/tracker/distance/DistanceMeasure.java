package app.tracker.distance;

import app.simConfig.SimConfig;
import app.tracker.JasimaEvolveExperiment;
import ec.EvolutionState;
import ec.Individual;

public interface DistanceMeasure {

	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final SimConfig simConfig,
			final Individual[] inds);

}
