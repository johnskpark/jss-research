package app.evolution.coop.niching;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.Individual;


public class ANHGPOverlapNiching extends CoopANHGPNiching {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] collaborators) {
		double[][] distances = new double[collaborators.length][collaborators.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the overlap between the individuals for the particular decision.
			boolean[][] overlaps = getOverlaps(decision, collaborators);

			// If the decisions do not overlap, then increment the distance between the two individuals.
			for (int i = 0; i < collaborators.length; i++) {
				for (int j = 0; j < collaborators.length; j++) {
					if (!overlaps[i][j]) {
						distances[i][j] += 1.0 / decisions.size();
					}
				}
			}
		}

		return distances;
	}

	protected boolean[][] getOverlaps(final JasimaEvolveDecision decision, final Individual[] collaborators) {
		boolean[][] overlaps = new boolean[collaborators.length][collaborators.length];

		JasimaPriorityStat[] stats = decision.getStats();

		for (int i = 0; i < collaborators.length; i++) {
			for (int j = 0; j < collaborators.length; j++) {
				if (i == j) { continue; }

				JasimaPriorityStat stat1 = stats[i];
				JasimaPriorityStat stat2 = stats[j];

				overlaps[i][j] = stat1.getBestEntry().equals(stat2.getBestEntry());
			}
		}

		return overlaps;
	}

}
