package app.evolution.coop.niching;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;
import java.util.Map;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;

public class ANHGPDecisionNiching extends CoopANHGPNiching {

	@Override
	public double[][] getDistances(final EvolutionState state,
			final JasimaEvolveExperiment experiment,
			final AbsSimConfig simConfig,
			final Individual[] collaborators) {
		double[][] distances = new double[collaborators.length][collaborators.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the ranks assigned to the individual voted job by the ensemble.
			double[] rankings = getEntryRankings(decision, collaborators);

			// Find the squared distances between the ranks.
			for (int i = 0; i < collaborators.length; i++) {
				for (int j = 0; j < collaborators.length; j++) {
					distances[i][j] += (rankings[i] - rankings[j]) * (rankings[i] - rankings[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < collaborators.length; i++) {
			for (int j = 0; j < collaborators.length; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}

	protected double[] getEntryRankings(final JasimaEvolveDecision decision, final Individual[] collaborators) {
		double[] rankings = new double[collaborators.length];

		List<PrioRuleTarget> entryRankings = decision.getEntryRankings();
		Map<GPIndividual, JasimaPriorityStat> decisionMakerMap = decision.getDecisionMakers();

		for (int i = 0; i < collaborators.length; i++) {
			JasimaPriorityStat stat = decisionMakerMap.get(collaborators[i]);

			rankings[i] = 1.0 * entryRankings.indexOf(stat.getBestEntry());
		}

		return rankings;
	}

}
