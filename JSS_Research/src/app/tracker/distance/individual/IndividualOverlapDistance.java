package app.tracker.distance.individual;

import java.util.List;

import app.ITrackedRule;
import app.simConfig.SimConfig;
import app.tracker.JasimaDecision;
import app.tracker.JasimaExperiment;
import app.tracker.JasimaPriorityStat;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;

public class IndividualOverlapDistance<T> implements DistanceMeasure<T> {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaExperiment<T> experiment,
			final SimConfig simConfig,
			final ITrackedRule<T> solver,
			final List<T> ruleComponents) {
		int numComponents = ruleComponents.size();
		double[][] distances = new double[numComponents][numComponents];

		List<JasimaDecision<T>> decisions = experiment.getDecisions();

		for (JasimaDecision<T> decision : decisions) {
			// Get the overlap between the individuals for the particular decision.
			boolean[][] overlaps = getOverlaps(decision, solver, ruleComponents, numComponents);

			// If the decisions do not overlap, then increment the distance between the two individuals.
			for (int i = 0; i < numComponents; i++) {
				for (int j = 0; j < numComponents; j++) {
					if (!overlaps[i][j]) {
						distances[i][j] += 1.0 / decisions.size();
					}
				}
			}
		}

		return distances;
	}

	protected boolean[][] getOverlaps(final JasimaDecision<T> decision,
			final ITrackedRule<T> solver,
			final List<T> ruleComponents,
			final int numComponents) {
		boolean[][] overlaps = new boolean[numComponents][numComponents];

		JasimaPriorityStat[] stats = decision.getStats(solver);

		for (int i = 0; i < numComponents; i++) {
			for (int j = 0; j < numComponents; j++) {
				if (i == j) { continue; }

				JasimaPriorityStat stat1 = stats[i];
				JasimaPriorityStat stat2 = stats[j];

				overlaps[i][j] = stat1.getBestEntry().equals(stat2.getBestEntry());
			}
		}

		return overlaps;
	}

}
