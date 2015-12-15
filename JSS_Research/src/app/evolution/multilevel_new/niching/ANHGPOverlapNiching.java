package app.evolution.multilevel_new.niching;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.multilevel_new.MLSSubpopulation;

public class ANHGPOverlapNiching extends MultilevelANHGPNiching {

	@Override
	public double[][] getDistances(EvolutionState state, JasimaEvolveExperiment experiment, AbsSimConfig simConfig,
			MLSSubpopulation group) {
		double[][] distances = new double[group.individuals.length][group.individuals.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the overlap between the individuals for the particular decision.
			boolean[][] overlaps = getOverlaps(decision, group);

			// If the decisions do not overlap, then increment the distance between the two individuals.
			for (int i = 0; i < group.individuals.length; i++) {
				for (int j = 0; j < group.individuals.length; j++) {
					if (!overlaps[i][j]) {
						distances[i][j] += 1.0 / decisions.size();
					}
				}
			}
		}

		return distances;
	}

	protected boolean[][] getOverlaps(final JasimaEvolveDecision decision, final MLSSubpopulation group) {
		boolean[][] overlaps = new boolean[group.individuals.length][group.individuals.length];

		JasimaPriorityStat[] stats = decision.getStats();

		for (int i = 0; i < group.individuals.length; i++) {
			for (int j = 0; j < group.individuals.length; j++) {
				if (i == j) { continue; }

				JasimaPriorityStat stat1 = stats[i];
				JasimaPriorityStat stat2 = stats[j];

				overlaps[i][j] = stat1.getBestEntry().equals(stat2.getBestEntry());
			}
		}

		return overlaps;
	}

}
