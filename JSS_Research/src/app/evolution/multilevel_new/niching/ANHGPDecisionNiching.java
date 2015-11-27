package app.evolution.multilevel_new.niching;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;
import java.util.Map;

import app.simConfig.AbsSimConfig;
import app.tracker.JasimaEvolveDecision;
import app.tracker.JasimaEvolveExperiment;
import app.tracker.JasimaPriorityStat;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.multilevel_new.MLSSubpopulation;

public class ANHGPDecisionNiching extends MultilevelANHGPNiching {

	@Override
	public double[][] getDistances(EvolutionState state, JasimaEvolveExperiment experiment, AbsSimConfig simConfig,
			MLSSubpopulation group) {
		double[][] distances = new double[group.individuals.length][group.individuals.length];

		List<JasimaEvolveDecision> decisions = experiment.getDecisions();

		for (JasimaEvolveDecision decision : decisions) {
			// Get the ranks assigned to the individual voted job by the ensemble.
			double[] rankings = getEntryRankings(decision, group);

			// Find the squared distances between the ranks.
			for (int i = 0; i < group.individuals.length; i++) {
				for (int j = 0; j < group.individuals.length; j++) {
					distances[i][j] += (rankings[i] - rankings[j]) * (rankings[i] - rankings[j]);
				}
			}
		}

		// Normalise the distances by taking the root mean of the distances over all decisions.
		for (int i = 0; i < group.individuals.length; i++) {
			for (int j = 0; j < group.individuals.length; j++) {
				distances[i][j] = Math.sqrt(distances[i][j] / decisions.size());
			}
		}

		return distances;
	}

	/**
	 * TODO javadoc.
	 */
	protected double[] getEntryRankings(final JasimaEvolveDecision decision, final MLSSubpopulation group) {
		double[] rankings = new double[group.individuals.length];

		List<PrioRuleTarget> entryRankings = decision.getEntryRankings();
		Map<GPIndividual, JasimaPriorityStat> decisionMakerMap = decision.getDecisionMakers();

		for (int i = 0; i < group.individuals.length; i++) {
			JasimaPriorityStat stat = decisionMakerMap.get(group.individuals[i]);

			rankings[i] = 1.0 * entryRankings.indexOf(stat.getBestEntry());
		}

		return rankings;
	}

}
