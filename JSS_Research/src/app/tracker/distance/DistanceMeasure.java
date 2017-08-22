package app.tracker.distance;

import java.util.List;

import app.ITrackedRule;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperiment;
import ec.EvolutionState;

public interface DistanceMeasure<T> {

	public double[][] getDistances(final EvolutionState state,
			final JasimaExperiment<T> experiment,
			final SimConfig simConfig,
			final ITrackedRule<T> solver,
			final List<T> ruleComponents);

}
