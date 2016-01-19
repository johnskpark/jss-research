package app.tracker.distance.individual;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import app.tracker.distance.DistanceMeasure;
import ec.EvolutionState;
import ec.Individual;

// TODO update this.
public class IndividualDecisionDistance implements DistanceMeasure {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] inds) {
		double[][] distances = new double[inds.length][inds.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the ranks assigned to the individual voted job by the ensemble.
			double[] rankings = getEntryRankings(decision, inds);

			// Find the squared distances between the ranks.
			for (int i = 0; i < inds.length; i++) {
				for (int j = 0; j < inds.length; j++) {
					distances[i][j] += (rankings[i] - rankings[j]) * (rankings[i] - rankings[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < inds.length; i++) {
			for (int j = 0; j < inds.length; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}

	protected double[] getEntryRankings(final JasimaEvolveDecision decision, final Individual[] inds) {
		double[] rankings = new double[inds.length];

		List<PrioRuleTarget> entryRankings = decision.getEntryRankings();
		JasimaPriorityStat[] stats = decision.getStats();

		for (int i = 0; i < inds.length; i++) {
			JasimaPriorityStat stat = stats[i];

			rankings[i] = 1.0 * entryRankings.indexOf(stat.getBestEntry());
		}

		return rankings;
	}

}
