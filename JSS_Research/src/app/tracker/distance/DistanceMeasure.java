package app.tracker.distance;

import app.simConfig.DynamicSimConfig;
import app.tracker.JasimaEvolveExperiment;
import ec.EvolutionState;
import ec.Individual;

public interface DistanceMeasure {

	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final DynamicSimConfig simConfig,
			final Individual[] inds);

}
