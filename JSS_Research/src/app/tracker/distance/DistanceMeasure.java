package app.tracker.distance;

import java.util.List;

import app.IMultiRule;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperiment;
import ec.EvolutionState;

public interface DistanceMeasure<T> {

	public double[][] getDistances(final EvolutionState state,
			final JasimaExperiment<T> experiment,
			final SimConfig simConfig,
			final IMultiRule<T> solver,
			final List<T> ruleComponents);

}
