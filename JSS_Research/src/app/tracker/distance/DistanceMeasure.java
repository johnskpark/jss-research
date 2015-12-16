package app.tracker.distance;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveExperiment;
import ec.EvolutionState;
import ec.Individual;

public interface DistanceMeasure {

	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] inds);

}
